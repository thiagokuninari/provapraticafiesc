package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoResponse;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import br.com.xbrain.autenticacao.modules.mailing.service.MailingService;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import com.querydsl.core.types.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado.ATIVO;
import static br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado.ESTADUAL;
import static br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado.NACIONAL;
import static br.com.xbrain.autenticacao.modules.feriado.helper.FeriadoHelper.*;
import static br.com.xbrain.autenticacao.modules.usuarioacesso.helper.UsuarioAcessoHelper.umUsuarioAgenteAutorizado;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FeriadoServiceTest {

    @InjectMocks
    private FeriadoService service;
    @Mock
    private CallService callService;
    @Mock
    private DataHoraAtual dataHoraAtual;
    @Mock
    private CidadeService cidadeService;
    @Mock
    private FeriadoRepository repository;
    @Mock
    private MailingService mailingService;
    @Mock
    private FeriadoHistoricoService historicoService;
    @Mock
    private AutenticacaoService autenticacaoService;

    @Test
    public void consulta_retornarTrue_quandoEncontrarFeriadoHoje() {
        when(repository.findByDataFeriadoAndFeriadoNacionalAndSituacao(dataHoraAtual.getData(), Eboolean.V,
            ATIVO)).thenReturn(Optional.of(umFeriado()));

        assertThat(service.consulta()).isTrue();

        verify(repository).findByDataFeriadoAndFeriadoNacionalAndSituacao(dataHoraAtual.getData(), Eboolean.V,
            ATIVO);
    }

    @Test
    public void consulta_retornarFalse_quandoNaoEncontrarFeriadoHoje() {
        when(repository.findByDataFeriadoAndFeriadoNacionalAndSituacao(dataHoraAtual.getData(), Eboolean.V,
            ATIVO)).thenReturn(Optional.empty());

        assertThat(service.consulta()).isFalse();

        verify(repository).findByDataFeriadoAndFeriadoNacionalAndSituacao(dataHoraAtual.getData(), Eboolean.V,
            ATIVO);
    }

    @Test
    public void consulta_retornarTrue_quandoEncontrarFeriadoNaDataInformada() {
        when(repository.findByDataFeriadoAndFeriadoNacionalAndSituacao(LocalDate.of(2023, 10, 12),
            Eboolean.V, ATIVO)).thenReturn(Optional.of(umFeriado()));

        assertThat(service.consulta("12/10/2023")).isTrue();

        verify(repository).findByDataFeriadoAndFeriadoNacionalAndSituacao(LocalDate.of(2023, 10, 12),
            Eboolean.V, ATIVO);
    }

    @Test
    public void consulta_retornarFalse_quandoNaoEncontrarFeriadoNaDataInformada() {
        when(repository.findByDataFeriadoAndFeriadoNacionalAndSituacao(LocalDate.of(2023, 10, 12),
            Eboolean.V, ATIVO)).thenReturn(Optional.empty());

        assertThat(service.consulta("12/10/2023")).isFalse();

        verify(repository).findByDataFeriadoAndFeriadoNacionalAndSituacao(LocalDate.of(2023, 10, 12),
            Eboolean.V, ATIVO);
    }

    @Test
    public void consulta_retornarTrue_quandoEncontrarFeriadoNaDataECidadeInformada() {
        when(cidadeService.findById(1))
            .thenReturn(umaCidade(1, "LONDRINA"));
        when(repository.existsByDataFeriadoAndCidadeIdOrUfId(
            LocalDate.of(2023, 10, 12), 1, 1, ATIVO)).thenReturn(true);

        assertThat(service.consulta("12/10/2023", 1)).isTrue();

        verify(repository).existsByDataFeriadoAndCidadeIdOrUfId(
            LocalDate.of(2023, 10, 12), 1, 1, ATIVO);
    }

    @Test
    public void consulta_retornarFalse_quandoNaoEncontrarFeriadoNaDataECidadeInformada() {
        when(cidadeService.findById(1))
            .thenReturn(umaCidade(1, "LONDRINA"));

        when(repository.existsByDataFeriadoAndCidadeIdOrUfId(LocalDate.of(2023, 10, 12),
            1, 1, ATIVO)).thenReturn(false);

        assertThat(service.consulta("12/10/2023", 1)).isFalse();

        verify(repository).existsByDataFeriadoAndCidadeIdOrUfId(LocalDate.of(2023, 10, 12),
            1, 1, ATIVO);
    }

    @Test
    public void save_deveSalvar_quandoDadosValidos() {
        when(repository.save(umFeriado())).thenReturn(umFeriado());

        assertThat(service.save(umFeriadoRequest())).isEqualTo(umFeriado());

        verify(repository).save(umFeriado());
    }

    @Test
    public void findAllByAnoAtual_deveRetornarFeriados_quandoChamado() {
        when(repository.findAllByAnoAtual(dataHoraAtual.getData())).thenReturn(List.of(umFeriado()));

        assertThat(service.findAllByAnoAtual()).isEqualTo(umIterableFeriado());

        verify(repository).findAllByAnoAtual(dataHoraAtual.getData());
    }

    @Test
    public void loadFeriados_deveCarregarOsFeriados_quandoChamado() {
        when(repository.findAllByAnoAtual(LocalDate.now())).thenReturn(List.of(umFeriado()));
        when(repository.findAllNacional(LocalDate.now()))
            .thenReturn(List.of(LocalDate.of(2023, 10, 11)));

        service.loadFeriados();

        verify(repository).findAllByAnoAtual(LocalDate.now());
        verify(repository).findAllNacional(LocalDate.now());
    }

    @Test
    public void isFeriadoHojeNaCidadeUf_retornarTrue_quandoEncontrarNacionalOuRegional() {
        when(repository.hasFeriadoEstadual(dataHoraAtual.getData(), "LONDRINA", "PR"))
            .thenReturn(Boolean.TRUE);

        assertThat(service.isFeriadoHojeNaCidadeUf("LONDRINA", "PR")).isTrue();

        verify(repository).hasFeriadoEstadual(dataHoraAtual.getData(), "LONDRINA", "PR");
    }

    @Test
    public void isFeriadoHojeNaCidadeUf_retornarFalse_quandoNaoEncontrarNacionalOuRegional() {
        when(repository.hasFeriadoEstadual(dataHoraAtual.getData(), "LONDRINA", "PR"))
            .thenReturn(Boolean.FALSE);

        assertThat(service.isFeriadoHojeNaCidadeUf("LONDRINA", "PR")).isFalse();

        verify(repository).hasFeriadoEstadual(dataHoraAtual.getData(), "LONDRINA", "PR");
    }

    @Test
    public void buscarUfsFeriadosEstaduaisPorData_retornarListaDeUfs_quandoChamado() {
        when(repository.buscarEstadosFeriadosEstaduaisPorData(dataHoraAtual.getData())).thenReturn(List.of("PR"));

        assertThat(service.buscarUfsFeriadosEstaduaisPorData()).isEqualTo(List.of("PR"));

        verify(repository).buscarEstadosFeriadosEstaduaisPorData(dataHoraAtual.getData());
    }

    @Test
    public void buscarFeriadosMunicipaisPorDataAtualUfs_retornarListaDeFeriados_quandoChamado() {
        when(repository.buscarFeriadosMunicipaisPorData(dataHoraAtual.getData()))
            .thenReturn(List.of(umFeriadoCidadeEstadoResponse()));

        assertThat(service.buscarFeriadosMunicipaisPorDataAtualUfs())
            .isEqualTo(List.of(umFeriadoCidadeEstadoResponse()));

        verify(repository).buscarFeriadosMunicipaisPorData(dataHoraAtual.getData());
    }

    @Test
    public void obterFeriadosByFiltros_retornarListaDeFeriados_quandoChamado() {
        when(repository.findAll(any(Predicate.class), any(PageRequest.class))).thenReturn(umaPaginaFeriado());

        var pageFeriadoResponse = umaPaginaFeriado().map(FeriadoResponse::of);

        assertThat(service.obterFeriadosByFiltros(new PageRequest(), new FeriadoFiltros()))
            .isEqualTo(pageFeriadoResponse);

        verify(repository).findAll(any(Predicate.class), any(PageRequest.class));
    }

    @Test
    public void getFeriadoById_retornarFeriado_quandoChamado() {
        when(repository.findById(1)).thenReturn(Optional.of(umFeriado()));

        assertThat(service.getFeriadoById(1))
            .isEqualTo(umFeriadoResponse());

        verify(repository).findById(1);
    }

    @Test
    public void getFeriadoById_retornarFeriadoComCidade_quandoFkCidadeExistir() {
        var feriado = umFeriado();
        feriado.getCidade().setFkCidade(1);

        when(repository.findById(1)).thenReturn(Optional.of(feriado));
        when(cidadeService.getCidadeById(1)).thenReturn(umaCidadeResponse());

        var response = umFeriadoResponse();
        response.setFkCidade(1);
        response.setCidadePai("MARINGA");

        assertThat(service.getFeriadoById(1))
            .isEqualTo(response);

        verify(repository).findById(1);
        verify(cidadeService).getCidadeById(1);
    }

    @Test
    public void salvarFeriado_deveSalvarFeriado_quandoDadosValidos() {
        when(repository.save(umFeriado())).thenReturn(umFeriado());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAgenteAutorizado());

        assertThat(service.salvarFeriado(umFeriadoRequest()))
            .isEqualTo(umFeriadoResponse());

        verify(repository).save(umFeriado());
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(historicoService).salvarHistorico(umFeriado(), "CADASTRADO MANUAL",
            umUsuarioAgenteAutorizado());
    }

    @Test
    public void salvarFeriado_deveLancarException_quandoEstadoNaoForNullEFeriadoNacional() {
        var request = umFeriadoRequest();
        request.setEstadoId(1);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarFeriado(request))
            .withMessage("Para este Tipo de Feriado não é permitido cadastrar ESTADO.");

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void salvarFeriado_deveLancarException_quandoCidadeNaoForNullEFeriadoNacional() {
        var request = umFeriadoRequest();
        request.setCidadeId(1);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarFeriado(request))
            .withMessage("Para este Tipo de Feriado não é permitido cadastrar CIDADE.");

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void salvarFeriado_deveLancarException_quandoEstadoForNullEFeriadoEstadual() {
        var request = umFeriadoRequest();
        request.setTipoFeriado(ESTADUAL);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarFeriado(request))
            .withMessage("Para este Tipo de Feriado o campo ESTADO é obrigatório.");

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void salvarFeriado_deveLancarException_quandoCidadeNaoForNullEFeriadoEstadual() {
        var request = umFeriadoRequest();
        request.setTipoFeriado(ESTADUAL);
        request.setEstadoId(1);
        request.setCidadeId(1);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarFeriado(request))
            .withMessage("Para este Tipo de Feriado não é permitido cadastrar CIDADE.");

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void salvarFeriado_deveLancarException_quandoEstadoForNullEFeriadoMunicipal() {
        var request = umFeriadoRequest();
        request.setTipoFeriado(ETipoFeriado.MUNICIPAL);
        request.setCidadeId(1);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarFeriado(request))
            .withMessage("Para este Tipo de Feriado o campo ESTADO é obrigatório.");

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void salvarFeriado_deveLancarException_quandoCidadeForNullEFeriadoMunicipal() {
        var request = umFeriadoRequest();
        request.setTipoFeriado(ETipoFeriado.MUNICIPAL);
        request.setEstadoId(1);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarFeriado(request))
            .withMessage("Para este Tipo de Feriado o campo CIDADE é obrigatório.");

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void salvarFeriado_deveLancarException_quandoFeriadoJaCadastrado() {
        when(repository.existsByPredicate(any())).thenReturn(true);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarFeriado(umFeriadoRequest()))
            .withMessage("Já existe feriado com os mesmos dados.");

        verify(repository).existsByPredicate(any());
        verify(repository, never()).save(any(Feriado.class));
    }

    @Test
    public void salvarFeriadoImportado_deveSalvarFeriados_quandoDadosValidos() {
        when(repository.save(umFeriado())).thenReturn(umFeriado());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAgenteAutorizado());

        assertThat(service.salvarFeriadoImportado(umFeriadoImportacao()))
            .isEqualTo(umFeriado());

        verify(repository).save(umFeriado());
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(historicoService).salvarHistorico(umFeriado(), "IMPORTADO",
            umUsuarioAgenteAutorizado());
    }

    @Test
    public void salvarFeriadoImportado_deveSalvarFeriados_quandoFeriadoEstadualEUploadAsyncTrue() {
        var feriado = umFeriado();
        feriado.setTipoFeriado(ESTADUAL);

        ReflectionTestUtils.setField(service, "uploadAsync", true);
        when(repository.save(feriado)).thenReturn(feriado);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAgenteAutorizado());

        assertThat(service.salvarFeriadoImportado(umFeriadoImportacao()))
            .isEqualTo(umFeriado());

        verify(repository).save(umFeriado());
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(historicoService).salvarHistorico(umFeriado(), "IMPORTADO",
            umUsuarioAgenteAutorizado());
    }

    @Test
    public void salvarFeriadoImportado_deveSalvarFeriados_quandoFeriadoEstadualEUploadAsyncFalse() {
        var feriado = umFeriado();
        feriado.setTipoFeriado(ESTADUAL);

        ReflectionTestUtils.setField(service, "uploadAsync", false);
        when(repository.save(feriado)).thenReturn(feriado);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAgenteAutorizado());

        assertThat(service.salvarFeriadoImportado(umFeriadoImportacao()))
            .isEqualTo(umFeriado());

        verify(repository).save(umFeriado());
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(historicoService).salvarHistorico(umFeriado(), "IMPORTADO",
            umUsuarioAgenteAutorizado());
        verify(cidadeService).getAllCidadeByUf(1);
    }

    @Test
    public void editarFeriado_deveEditarFeriado_quandoDadosValidos() {
        var request = umFeriadoRequest();
        request.setId(1);

        when(repository.findById(1)).thenReturn(Optional.of(umFeriado()));
        when(repository.save(umFeriado())).thenReturn(umFeriado());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAgenteAutorizado());

        assertThat(service.editarFeriado(request))
            .isEqualTo(umFeriadoResponse());

        verify(repository).findById(1);
        verify(repository).save(umFeriado());
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(historicoService).salvarHistorico(umFeriado(), "EDITADO",
            umUsuarioAgenteAutorizado());
    }

    @Test
    public void editarFeriado_deveEditarFeriado_quandoDadosTipoFeriadoEstadual() {
        var request = umFeriadoRequest();
        request.setId(1);
        request.setTipoFeriado(ESTADUAL);
        request.setEstadoId(3);

        var feriado = umFeriado();
        feriado.setTipoFeriado(ESTADUAL);
        feriado.setId(1);

        var feriadoPai = umFeriado("FERIADO PAI");
        feriadoPai.setSituacao(ESituacaoFeriado.ATIVO);

        when(repository.findById(1)).thenReturn(Optional.of(feriado));
        when(repository.save(feriado)).thenReturn(feriado);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAgenteAutorizado());

        assertThat(service.editarFeriado(request))
            .extracting("nome", "cidadeId", "cidadeNome", "tipoFeriado")
            .containsExactly("FERIADO TESTE", 1, "LONDRINA", ESTADUAL);

        verify(repository).findById(1);
        verify(repository).save(feriado);
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(historicoService).salvarHistorico(feriado, "EDITADO",
            umUsuarioAgenteAutorizado());
        verify(cidadeService).getAllCidadeByUf(1);
    }

    @Test
    public void editarFeriado_deveEditarFeriado_quandoUfDaRequestDiferenteDoFeriadoEncontrado() {
        var request = umFeriadoRequest();
        request.setId(1);
        request.setTipoFeriado(ESTADUAL);
        request.setEstadoId(3);

        var feriado = umFeriado();
        feriado.setTipoFeriado(ESTADUAL);
        feriado.setId(1);
        feriado.getUf().setId(3);

        var feriadoPai = umFeriado("FERIADO PAI");
        feriadoPai.setSituacao(ESituacaoFeriado.ATIVO);

        when(repository.findById(1)).thenReturn(Optional.of(feriado));
        when(repository.save(feriado)).thenReturn(feriado);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAgenteAutorizado());

        assertThat(service.editarFeriado(request))
            .extracting("nome", "cidadeId", "cidadeNome", "tipoFeriado")
            .containsExactly("FERIADO TESTE", 1, "LONDRINA", ESTADUAL);

        verify(repository).findById(1);
        verify(repository).save(feriado);
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(historicoService).salvarHistorico(feriado, "EDITADO",
            umUsuarioAgenteAutorizado());
    }

    @Test
    public void editarFeriado_deveLancarException_quandoDadosTipoFeriadoNaRequestForDiferenteDoFeriadoEncontrado() {
        var request = umFeriadoRequest();
        request.setId(1);
        request.setTipoFeriado(NACIONAL);
        var feriado = umFeriado();
        feriado.setTipoFeriado(ESTADUAL);

        when(repository.findById(1)).thenReturn(Optional.of(feriado));

        assertThatCode(() -> service.editarFeriado(request))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Não é permitido editar o Tipo do Feriado.");

        verify(repository).findById(1);
        verify(repository, never()).save(feriado);
        verify(historicoService, never()).salvarHistorico(feriado, "EDITADO",
            umUsuarioAgenteAutorizado());
    }

    @Test
    public void editarFeriado_deveLancarException_quandoEstadoNaoForNullEFeriadoNacional() {
        var request = umFeriadoRequest();
        request.setEstadoId(1);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.editarFeriado(request))
            .withMessage("Para este Tipo de Feriado não é permitido cadastrar ESTADO.");

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void editarFeriado_deveLancarException_quandoCidadeNaoForNullEFeriadoNacional() {
        var request = umFeriadoRequest();
        request.setCidadeId(1);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.editarFeriado(request))
            .withMessage("Para este Tipo de Feriado não é permitido cadastrar CIDADE.");

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void editarFeriado_deveLancarException_quandoEstadoForNullEFeriadoEstadual() {
        var request = umFeriadoRequest();
        request.setTipoFeriado(ESTADUAL);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.editarFeriado(request))
            .withMessage("Para este Tipo de Feriado o campo ESTADO é obrigatório.");

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void editarFeriado_deveLancarException_quandoCidadeNaoForNullEFeriadoEstadual() {
        var request = umFeriadoRequest();
        request.setTipoFeriado(ESTADUAL);
        request.setEstadoId(1);
        request.setCidadeId(1);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.editarFeriado(request))
            .withMessage("Para este Tipo de Feriado não é permitido cadastrar CIDADE.");

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void editarFeriado_deveLancarException_quandoEstadoForNullEFeriadoMunicipal() {
        var request = umFeriadoRequest();
        request.setTipoFeriado(ETipoFeriado.MUNICIPAL);
        request.setCidadeId(1);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.editarFeriado(request))
            .withMessage("Para este Tipo de Feriado o campo ESTADO é obrigatório.");

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void editarFeriado_deveLancarException_quandoCidadeForNullEFeriadoMunicipal() {
        var request = umFeriadoRequest();
        request.setTipoFeriado(ETipoFeriado.MUNICIPAL);
        request.setEstadoId(1);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.editarFeriado(request))
            .withMessage("Para este Tipo de Feriado o campo CIDADE é obrigatório.");

        verifyNoMoreInteractions(repository);
    }

    @Test
    public void editarFeriado_deveLancarException_quandoFeriadoJaCadastrado() {
        when(repository.existsByPredicate(any())).thenReturn(true);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.editarFeriado(umFeriadoRequest()))
            .withMessage("Já existe feriado com os mesmos dados.");

        verify(repository).existsByPredicate(any());
        verify(repository, never()).save(any(Feriado.class));
    }

    @Test
    public void editarFeriado_deveLancarException_quandoNaoEncontrarFeriado() {
        var request = umFeriadoRequest();
        request.setId(1);

        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.editarFeriado(request))
            .withMessage("Feriado não encontrado.");

        verify(repository).findById(1);
        verify(repository, never()).save(any(Feriado.class));
        verify(autenticacaoService, never()).getUsuarioAutenticado();
        verify(historicoService, never()).salvarHistorico(any(), anyString(), any());
    }

    @Test
    public void excluirFeriado_deveExcluirFeriado_quandoDadosValidos() {
        var request = umFeriadoRequest();
        request.setId(1);

        when(repository.findById(1)).thenReturn(Optional.of(umFeriado()));
        when(repository.save(umFeriado())).thenReturn(umFeriado());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAgenteAutorizado());

        service.excluirFeriado(1);

        verify(repository).findById(1);
        verify(repository).save(umFeriado());
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(historicoService).salvarHistorico(umFeriado(), "EXCLUIDO", umUsuarioAgenteAutorizado());
    }

    @Test
    public void excluirFeriado_deveLancarException_quandoNaoEncontrarFeriado() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.excluirFeriado(1))
            .withMessage("Feriado não encontrado.");

        verify(repository).findById(1);
        verify(repository, never()).save(any(Feriado.class));
        verify(autenticacaoService, never()).getUsuarioAutenticado();
        verify(historicoService, never()).salvarHistorico(any(), anyString(), any());
    }

    @Test
    public void findById_deveBuscarPorId_quandoEncontrar() {
        when(repository.findById(1)).thenReturn(Optional.of(umFeriado()));

        assertThat(service.findById(1)).isEqualTo(umFeriado());

        verify(repository).findById(1);
    }

    @Test
    public void findById_deveLancarException_quandoNaoEncontrar() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.findById(1))
            .withMessage("Feriado não encontrado.");

        verify(repository).findById(1);
    }

    @Test
    public void flushCacheFeriados_deveCarregarCacheDosFeriados_quandoChamado() {
        when(repository.findAllByAnoAtual(LocalDate.now())).thenReturn(List.of(umFeriado()));
        when(repository.findAllNacional(LocalDate.now()))
            .thenReturn(List.of(LocalDate.of(2023, 10, 11)));

        service.flushCacheFeriados();

        verify(repository).findAllByAnoAtual(LocalDate.now());
        verify(repository).findAllNacional(LocalDate.now());
    }

    @Test
    public void buscarTotalDeFeriadosPorMesAno_deveRetornarListaDeFeriados_quandoChamado() {
        when(repository.buscarTotalDeFeriadosPorMesAno()).thenReturn(List.of(umFeriadoMesAnoResponse()));

        assertThat(service.buscarTotalDeFeriadosPorMesAno())
            .isEqualTo(List.of(umFeriadoMesAnoResponse()));

        verify(repository).buscarTotalDeFeriadosPorMesAno();
    }

    @Test
    public void isFeriadoComCidadeId_deveRetornarTrue_quandoTiverFeriadoNaCidade() {
        var data = LocalDate.of(2024, 9,8);
        when(dataHoraAtual.getData()).thenReturn(data);
        when(repository.hasFeriadoByCidadeIdAndDataAtual(1, data))
            .thenReturn(true);

        assertThat(service.isFeriadoComCidadeId(1))
            .isTrue();

        verify(repository).hasFeriadoByCidadeIdAndDataAtual(1, data);
    }

    @Test
    public void isFeriadoComCidadeId_deveRetornarFalse_quandoNaoTiverFeriadoNaCidade() {
        var data = LocalDate.of(2024, 9,8);
        when(dataHoraAtual.getData()).thenReturn(data);
        when(repository.hasFeriadoByCidadeIdAndDataAtual(1, data))
            .thenReturn(false);

        assertThat(service.isFeriadoComCidadeId(1))
            .isFalse();

        verify(repository).hasFeriadoByCidadeIdAndDataAtual(1, data);
    }

    @Test
    public void flushCacheFeriadoTelefonia_deveLimparCacheFeriados_quandoChamado() {
        service.flushCacheFeriadoTelefonia();

        verify(callService).cleanCacheFeriadosTelefonia();
    }

    @Test
    public void flushCacheFeriadoMailing_deveLimparCacheFeriados_quandoChamado() {
        service.flushCacheFeriadoMailing();

        verify(mailingService).flushCacheFeriadosMailing();
    }

    private CidadeResponse umaCidadeResponse() {
        var cidadeResponse = new CidadeResponse();
        cidadeResponse.setFkCidade(1);
        cidadeResponse.setNome("MARINGA");
        return cidadeResponse;
    }
}
