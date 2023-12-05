package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.client.AgenteAutorizadoNovoClient;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.InativarColaboradorMqSender;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioLogadoRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.UsuarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import br.com.xbrain.autenticacao.modules.usuarioacesso.repository.UsuarioAcessoRepository;
import com.querydsl.core.types.Predicate;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.helper.SolicitacaoRamalHelper.umaListaUsuarioResponse;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioDto;
import static br.com.xbrain.autenticacao.modules.usuarioacesso.helper.UsuarioAcessoHelper.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioAcessoServiceTest {

    @InjectMocks
    private UsuarioAcessoService service;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private UsuarioAcessoRepository usuarioAcessoRepository;
    @Mock
    private UsuarioHistoricoService usuarioHistoricoService;
    @Mock
    private AgenteAutorizadoNovoClient agenteAutorizadoNovoClient;
    @Mock
    private InativarColaboradorMqSender inativarColaboradorMqSender;
    @Mock
    private NotificacaoUsuarioAcessoService notificacaoUsuarioAcessoService;

    @Test
    public void registrarAcesso_deveRegistrarAcesso_quandoUsuarioEfetuarLogin() {
        assertThatCode(() -> service.registrarAcesso(100))
            .doesNotThrowAnyException();

        verify(usuarioAcessoRepository)
            .save(any(UsuarioAcesso.class));
    }

    @Test
    public void registrarLogout_deveRegistrarOlogoutDoUsuario_quandoDeslogarDoSistema() {
        assertThatCode(() -> service.registrarLogout(100))
            .doesNotThrowAnyException();

        verify(usuarioAcessoRepository)
            .save(any(UsuarioAcesso.class));
    }

    @Test
    public void inativarUsuariosSemAcesso_naoDeveLancarException_quandoCairNoCatch() {
        assertThatCode(() -> service.inativarUsuariosSemAcesso("TESTE"))
            .doesNotThrowAnyException();
    }

    @Test
    public void inativarUsuariosSemAcesso_deveInativarUsuarios_quandoNaoEfetuarLoginPorTrintaEDoisDias() {
        ReflectionTestUtils.setField(service, "dataHoraInativarUsuario", "2021-05-30T00:00:00.000");
        when(usuarioRepository.findAllUltimoAcessoUsuariosComDataReativacaoDepoisTresDiasAndNotViabilidade(
            LocalDateTime.of(2021, 5, 30, 0, 0)))
            .thenReturn(List.of(umUsuarioDto(1)));
        when(usuarioRepository.findAllUsuariosSemDataUltimoAcessoAndDataReativacaoDepoisTresDiasAndNotViabilidade(
            LocalDateTime.of(2021, 5, 30, 0, 0)))
            .thenReturn(List.of(umUsuarioDto(2)));

        assertThat(service.inativarUsuariosSemAcesso("TESTE"))
            .isEqualTo(2);

        verify(usuarioRepository).findAllUltimoAcessoUsuariosComDataReativacaoDepoisTresDiasAndNotViabilidade(
            LocalDateTime.of(2021, 5, 30, 0, 0));
        verify(usuarioRepository).findAllUsuariosSemDataUltimoAcessoAndDataReativacaoDepoisTresDiasAndNotViabilidade(
            LocalDateTime.of(2021, 5, 30, 0, 0));
        verify(usuarioRepository).atualizarParaSituacaoInativo(1);
        verify(usuarioHistoricoService).gerarHistoricoInativacao(1, "TESTE");
        verify(usuarioRepository).atualizarParaSituacaoInativo(2);
        verify(usuarioHistoricoService).gerarHistoricoInativacao(2, "TESTE");
    }

    @Test
    public void deletarHistoricoUsuarioAcesso_deveDeletarRegistros_quandoDataCadastroUltrapassarDoisMeses() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado("XBRAIN"));

        assertThat(service.deletarHistoricoUsuarioAcesso()).isZero();

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(usuarioAcessoRepository).deletarHistoricoUsuarioAcesso();
    }

    @Test
    public void deletarHistoricoUsuarioAcesso_deveLancarException_quandoTentarDeletarHistoricoSemPermissaoXbrain() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado("MSO"));

        assertThatExceptionOfType(PermissaoException.class)
            .isThrownBy(() -> service.deletarHistoricoUsuarioAcesso());

        verify(autenticacaoService).getUsuarioAutenticado();
        verifyNoMoreInteractions(usuarioAcessoRepository);
    }

    @Test
    public void getAll_deveBuscarTodosUsuarios_quandoChamado() {
        when(usuarioAcessoRepository.findAll(new UsuarioAcessoFiltros().toPredicate(), new PageRequest()))
            .thenReturn(umaPaginaUsuarioAcesso());

        assertThat(service.getAll(new PageRequest(), new UsuarioAcessoFiltros()))
            .isEqualTo(umaPaginaUsuarioAcessoResponse());

        verify(usuarioAcessoRepository).findAll(new UsuarioAcessoFiltros().toPredicate(), new PageRequest());
    }

    @Test
    public void getAll_deveBuscarTodosUsuarios_quandoPassarIdNoFiltro() {
        var filtro = new UsuarioAcessoFiltros();
        filtro.setAaId(1);

        var predicate = filtro;
        predicate.setAgenteAutorizadosIds(List.of(1, 2));

        when(agenteAutorizadoNovoClient.getUsuariosByAaId(1, false))
            .thenReturn(umaListaUsuarioResponse());
        when(usuarioAcessoRepository.findAll(predicate.toPredicate(), new PageRequest()))
            .thenReturn(umaPaginaUsuarioAcesso());

        assertThat(service.getAll(new PageRequest(), filtro))
            .isEqualTo(umaPaginaUsuarioAcessoResponse());

        verify(agenteAutorizadoNovoClient).getUsuariosByAaId(1, false);
        verify(usuarioAcessoRepository).findAll(predicate.toPredicate(), new PageRequest());
    }

    @Test
    public void exportRegistrosToCsv_deveExportCsv_quandoDadosValidos() {
        when(usuarioAcessoRepository.findAll(new UsuarioAcessoFiltros().toPredicate()))
            .thenReturn(List.of(umUsuarioAcesso()));

        assertThatCode(() -> service.exportRegistrosToCsv(new MockHttpServletResponse(), new UsuarioAcessoFiltros()))
            .doesNotThrowAnyException();

        verify(usuarioAcessoRepository).findAll(new UsuarioAcessoFiltros().toPredicate());
    }

    @Test
    public void exportRegistrosToCsv_deveLancarException_quandoErroNaResponse() {
        when(usuarioAcessoRepository.findAll(new UsuarioAcessoFiltros().toPredicate()))
            .thenReturn(List.of(umUsuarioAcesso()));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.exportRegistrosToCsv(null, new UsuarioAcessoFiltros()))
            .withMessage("Falha ao tentar baixar relatório.");

        verify(usuarioAcessoRepository).findAll(new UsuarioAcessoFiltros().toPredicate());
    }

    @Test
    public void getRegistros_retornaRegistrosOrdenados_quandoExistir() {
        var listaUsuarioAcesso = List.of(umUsuarioAcesso(1, 14, 29),
            umUsuarioAcesso(1, 14, 29),
            umUsuarioAcesso(3, 13, 29),
            umUsuarioAcesso(2, 16, 28));

        when(usuarioAcessoRepository.findAll(umUsuarioAcessoFiltros().toPredicate()))
            .thenReturn(listaUsuarioAcesso);

        assertThat(service.getRegistros(umUsuarioAcessoFiltros()))
            .extracting("id", "dataHora")
            .containsExactly(
                Tuple.tuple(1, "29/01/2020 14:00:00"),
                Tuple.tuple(3, "29/01/2020 13:00:00"),
                Tuple.tuple(2, "28/01/2020 16:00:00")
            );

        verify(usuarioAcessoRepository).findAll(umUsuarioAcessoFiltros().toPredicate());
    }

    @Test
    public void getRegistros_retornaRegistrosOrdenados_quando() {
        var filtro = new UsuarioAcessoFiltros();
        filtro.setAaId(1);
        var predicate = filtro;
        predicate.setAgenteAutorizadosIds(List.of(1, 2));
        var listaUsuarioAcesso = List.of(umUsuarioAcesso(1, 14, 29),
            umUsuarioAcesso(1, 14, 29),
            umUsuarioAcesso(3, 13, 29),
            umUsuarioAcesso(2, 16, 28));

        when(agenteAutorizadoNovoClient.getUsuariosByAaId(1, false))
            .thenReturn(umaListaUsuarioResponse());
        when(usuarioAcessoRepository.findAll(predicate.toPredicate()))
            .thenReturn(listaUsuarioAcesso);

        assertThat(service.getRegistros(filtro))
            .extracting("id", "dataHora")
            .containsExactly(
                Tuple.tuple(1, "29/01/2020 14:00:00"),
                Tuple.tuple(3, "29/01/2020 13:00:00"),
                Tuple.tuple(2, "28/01/2020 16:00:00")
            );

        verify(agenteAutorizadoNovoClient).getUsuariosByAaId(1, false);
        verify(usuarioAcessoRepository).findAll(predicate.toPredicate());
    }

    @Test
    public void getCsv_retornaStringCsvComMensagemDeRegistroNaoEncontrado_quandoNaoEncontrarRegistros() {
        List<UsuarioAcessoResponse> listaUsuarioAcesso = List.of();

        assertThat(service.getCsv(listaUsuarioAcesso))
            .isEqualTo("ID;NOME;CPF;E-MAIL;DATA;\n"
                + "Registros não encontrados.");
    }

    @Test
    public void getTotalUsuariosLogadosPorPeriodoByFiltros_totalUsuariosLogadosDeAcordoComFiltro_quandoExistirUsuariosLogados() {
        when(usuarioRepository.findAll(any(Predicate.class))).thenReturn(List.of(new Usuario(101)));
        when(notificacaoUsuarioAcessoService.countUsuariosLogadosPorPeriodo(any())).thenReturn(umUsuarioLogadoResponse());

        var response = service.getTotalUsuariosLogadosPorPeriodoByFiltros(umUsuarioLogadoRequestComPeriodo());

        assertThat(response)
            .extracting("dataInicial", "dataFinal", "totalUsuariosLogados")
            .containsExactly(
                tuple("2020-12-01T10:00:00.000Z", "2020-12-01T10:59:59.999Z", 10),
                tuple("2020-12-01T11:00:00.000Z", "2020-12-01T11:42:39.999Z", 3)
            );

        verify(notificacaoUsuarioAcessoService)
            .countUsuariosLogadosPorPeriodo(any(UsuarioLogadoRequest.class));
    }

    @Test
    public void getTotalUsuariosLogadosPorPeriodoByFiltros_deveRetornarPeriodosComTotalUsuariosZero_quandoNaoExistirUsuario() {
        when(usuarioRepository.findAll(any(Predicate.class))).thenReturn(List.of());

        var response = service.getTotalUsuariosLogadosPorPeriodoByFiltros(umUsuarioLogadoRequestComPeriodo());

        assertThat(response)
            .extracting("dataInicial", "dataFinal", "totalUsuariosLogados")
            .containsExactlyInAnyOrder(
                tuple("2020-12-01T10:00:00.000Z", "2020-12-01T10:59:59.999Z", 0),
                tuple("2020-12-01T11:00:00.000Z", "2020-12-01T11:42:39.999Z", 0));

        verify(notificacaoUsuarioAcessoService, never()).countUsuariosLogadosPorPeriodo(any());
    }

    @Test
    public void getUsuariosLogadosAtualPorIds_idsDosUsuariosLogadosDeAcordoComFiltro_quandoExistirUsuariosLogados() {
        when(usuarioRepository.findAll(any(Predicate.class))).thenReturn(List.of(new Usuario(101), new Usuario(201)));
        when(notificacaoUsuarioAcessoService.getUsuariosLogadosAtualPorIds(List.of(101, 201))).thenReturn(List.of(101));

        var response = service.getUsuariosLogadosAtualPorIds(
            UsuarioLogadoRequest.builder()
                .organizacaoId(6)
                .cargos(List.of(CodigoCargo.BACKOFFICE_OPERADOR_TRATAMENTO))
                .build());
        var predicate = new UsuarioPredicate()
            .comOrganizacaoEmpresaId(6)
            .comCodigosCargos(List.of(CodigoCargo.BACKOFFICE_OPERADOR_TRATAMENTO))
            .isAtivo(Eboolean.V)
            .build();

        assertThat(response).containsExactly(101);

        verify(usuarioRepository).findAll(predicate);
        verify(notificacaoUsuarioAcessoService).getUsuariosLogadosAtualPorIds(List.of(101, 201));
    }
}
