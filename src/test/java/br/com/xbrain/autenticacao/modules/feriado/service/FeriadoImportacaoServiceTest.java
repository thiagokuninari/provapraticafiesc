package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.feriado.predicate.FeriadoPredicate;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

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
        when(feriadoRepository.findByPredicate(predicate)).thenReturn(Optional.of(umFeriado()));

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
}
