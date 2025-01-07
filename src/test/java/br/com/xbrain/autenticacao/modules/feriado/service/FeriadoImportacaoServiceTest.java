package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.predicate.FeriadoPredicate;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.feriado.helper.FeriadoHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class FeriadoImportacaoServiceTest {

    @InjectMocks
    private FeriadoImportacaoService service;
    @Mock
    private FeriadoService feriadoService;
    @Mock
    private FeriadoRepository feriadoRepository;
    @Mock
    private UfRepository ufRepository;
    @Mock
    private CidadeService cidadeService;

    @Test
    public void importarFeriadoArquivo_deveImportarFeriadosCorretosEMarcarLinhasComErros_quandoDadosValidos() {
        var feriado = umFeriadoImportacao("FERIADO EXISTENTE");
        var predicate = new FeriadoPredicate()
            .comNome(feriado.getNome())
            .comTipoFeriado(feriado.getTipoFeriado())
            .comEstado(!isEmpty(feriado.getUf()) ? feriado.getUf().getId() : null)
            .comCidade(!isEmpty(feriado.getCidade()) ? feriado.getCidade().getId() : null,
                !isEmpty(feriado.getUf()) ? feriado.getUf().getId() : null)
            .comDataFeriado(feriado.getDataFeriado())
            .excetoExcluidos()
            .excetoFeriadosFilhos()
            .build();

        when(feriadoService.salvarFeriadoImportado(umFeriadoImportacao("FERIADO CORRETO")))
            .thenReturn(umFeriado("FERIADO CORRETO"));
        when(feriadoRepository.existsByPredicate(predicate)).thenReturn(true);

        assertThat(service.importarFeriadoArquivo(umFileFeriado(), umFeriadoImportacaoRequest()))
            .isEqualTo(umaListaFeriadoImportacaoResponse());

        verify(feriadoService).salvarFeriadoImportado(umFeriadoImportacao("FERIADO CORRETO"));
    }

    @Test
    public void importarFeriadoArquivo_deveLancarException_quandoLinhasVazias() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.importarFeriadoArquivo(umFile(""), umFeriadoImportacaoRequest()))
            .withMessage("O arquivo não pode ser vazio.");

        verifyNoMoreInteractions(feriadoService);
    }

    @Test
    public void importarFeriadoArquivo_deveLancarException_quandoCabecalhoInvalido() {
        String file = "col1;col2;col3\n"
            + "val1;val2;val3\n";

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.importarFeriadoArquivo(umFile(file), umFeriadoImportacaoRequest()))
            .withMessage("O cabeçalho do arquivo não pode ser diferente do exemplo.");

        verifyNoMoreInteractions(feriadoService);
    }

    @Test
    public void importarFeriado_deveImportarFeriado_quandoDadosValidos() {
        var linhaFeriado = new String[]{"NACIONAL", "LONDRINA", "PR", "12/10/2019", "FERIADO CORRETO"};
        when(feriadoService.salvarFeriadoImportado(umFeriadoImportacao("FERIADO CORRETO")))
            .thenReturn(umFeriado());

        assertThat(service.importarFeriado(linhaFeriado, 2019))
            .isEqualTo(umFeriadoImportacaoResponse());

        verify(feriadoService).salvarFeriadoImportado(umFeriadoImportacao("FERIADO CORRETO"));
    }

    @Test
    public void importarFeriado_deveImportarFeriado_quandoFeriadoTiverUf() {
        var feriadoImportacao = umFeriadoImportacao("FERIADO CORRETO");
        feriadoImportacao.setTipoFeriado(ETipoFeriado.MUNICIPAL);

        var uf = Uf.builder().id(1).nome("PR").build();
        var cidade = Cidade.builder().uf(uf).nome("LONDRINA").build();
        feriadoImportacao.setUf(uf);
        feriadoImportacao.setCidade(cidade);

        when(cidadeService.findByUfNomeAndCidadeNome("PR", "LONDRINA")).thenReturn(cidade);
        when(ufRepository.findByUf("PR")).thenReturn(Optional.ofNullable(uf));
        when(feriadoService.salvarFeriadoImportado(feriadoImportacao))
            .thenReturn(umFeriadoMunicipalCidadeLondrina());

        var linhaFeriado = new String[]{"MUNICIPAL", "LONDRINA", "PR", "12/10/2019", "FERIADO CORRETO"};

        assertThat(service.importarFeriado(linhaFeriado, 2019))
            .extracting("nome", "cidadeId", "cidadeNome", "tipoFeriado", "motivoNaoImportacao",
                "estadoId", "estadoNome")
            .containsExactly("Aniversário da cidade", 5578, "LONDRINA", ETipoFeriado.MUNICIPAL, List.of(),
                1, "PARANA");

        verify(feriadoService).salvarFeriadoImportado(feriadoImportacao);
        verify(cidadeService).findByUfNomeAndCidadeNome("PR", "LONDRINA");
        verify(ufRepository).findByUf("PR");
    }

    @Test
    public void importarFeriado_deveImportarFeriadoComMotivoNaoImportacao_quandoCidadeNaoEncontrada() {
        var feriadoImportacao = umFeriadoImportacao("FERIADO CORRETO");
        feriadoImportacao.setTipoFeriado(ETipoFeriado.MUNICIPAL);

        var uf = Uf.builder().id(1).nome("PR").build();
        feriadoImportacao.setUf(uf);

        when(ufRepository.findByUf("PR")).thenReturn(Optional.ofNullable(uf));

        var linhaFeriado = new String[]{"MUNICIPAL", "LONDRINA", "PR", "12/10/2019", "FERIADO CORRETO"};
        assertThat(service.importarFeriado(linhaFeriado, 2019))
            .extracting("nome", "cidadeId", "cidadeNome", "tipoFeriado", "motivoNaoImportacao",
                "estadoId", "estadoNome")
            .containsExactly("FERIADO CORRETO", null, null, ETipoFeriado.MUNICIPAL, List.of("Falha ao recuperar Cidade."),
                1, "PR");

        verify(feriadoService, never()).salvarFeriadoImportado(feriadoImportacao);
        verify(cidadeService).findByUfNomeAndCidadeNome("PR", "LONDRINA");
        verify(ufRepository).findByUf("PR");
    }

    @Test
    public void importarFeriado_deveImportarFeriadoComMotivoNaoImportacao_quandoUfNaoEncontrada() {
        var feriadoImportacao = umFeriadoImportacao("FERIADO CORRETO");
        feriadoImportacao.setTipoFeriado(ETipoFeriado.MUNICIPAL);

        var uf = Uf.builder().id(1).nome("PR").build();
        var cidade = Cidade.builder().uf(uf).id(5578).nome("LONDRINA").build();
        feriadoImportacao.setCidade(cidade);

        when(cidadeService.findByUfNomeAndCidadeNome("PR", "LONDRINA")).thenReturn(cidade);

        var linhaFeriado = new String[]{"MUNICIPAL", "LONDRINA", "PR", "12/10/2019", "FERIADO CORRETO"};
        assertThat(service.importarFeriado(linhaFeriado, 2019))
            .extracting("nome", "cidadeId", "cidadeNome", "tipoFeriado", "motivoNaoImportacao",
                "estadoId", "estadoNome")
            .containsExactly("FERIADO CORRETO", 5578, "LONDRINA", ETipoFeriado.MUNICIPAL,
                List.of("Falha ao recuperar UF."), null, null);

        verify(feriadoService, never()).salvarFeriadoImportado(feriadoImportacao);
        verify(cidadeService).findByUfNomeAndCidadeNome("PR", "LONDRINA");
        verify(ufRepository).findByUf("PR");
    }

}
