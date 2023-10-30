package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.InativarColaboradorMqSender;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.InativarUsuarioFeederMqSender;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.PaLogadoDto;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioLogadoRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.enums.ETipo;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.UsuarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import br.com.xbrain.autenticacao.modules.usuarioacesso.repository.UsuarioAcessoRepository;
import com.querydsl.core.types.Predicate;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioDto;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioFeederDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioAcessoServiceTest {

    @InjectMocks
    private UsuarioAcessoService usuarioAcessoService;
    @Mock
    private UsuarioAcessoRepository usuarioAcessoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UsuarioHistoricoService usuarioHistoricoService;
    @Mock
    private InativarColaboradorMqSender inativarColaboradorMqSender;
    @Mock
    private InativarUsuarioFeederMqSender inativarUsuarioFeederMqSender;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private NotificacaoUsuarioAcessoService notificacaoUsuarioAcessoService;

    @Before
    public void setup() {
        when(usuarioRepository.findAllUltimoAcessoUsuariosComDataReativacaoDepoisTresDiasAndNotViabilidade(any()))
            .thenReturn(List.of(umUsuarioDto(102, "useremail@xbrain.com"),
                umUsuarioDto(103, "useremail@xbrain.com"),
                umUsuarioDto(104, "useremail@xbrain.com"),
                umUsuarioDto(105, "useremail@xbrain.com"),
                umUsuarioDto(106, "useremail@xbrain.com"),
                umUsuarioFeederDto(109, "useremail@xbrain.com")));

        when(usuarioRepository
            .findAllUsuariosSemDataUltimoAcessoAndDataReativacaoDepoisTresDiasAndNotViabilidade(any()))
            .thenReturn(List.of(umUsuarioDto(107, "useremail@xbrain.com"),
                umUsuarioDto(108, "useremail@xbrain.com"),
                umUsuarioFeederDto(110, "useremail@xbrain.com")));
    }

    @Test
    public void registrarAcesso_deveRegistrarAcesso_quandoUsuarioEfetuarLogin() {
        usuarioAcessoService.registrarAcesso(100);

        verify(usuarioAcessoRepository, times(1))
            .save(any(UsuarioAcesso.class));
    }

    @Test
    public void inativarUsuariosSemAcesso_deveInativarUsuarios_quandoNaoEfetuarLoginPorTrintaEDoisDias() {
        ReflectionTestUtils.setField(usuarioAcessoService, "dataHoraInativarUsuario", "2021-05-30T00:00:00.000");

        usuarioAcessoService.inativarUsuariosSemAcesso("TESTE");

        verify(usuarioRepository, times(9)).atualizarParaSituacaoInativo(anyInt());
        verify(usuarioHistoricoService, times(9)).gerarHistoricoInativacao(anyInt(), any(String.class));
        verify(inativarColaboradorMqSender, times(7)).sendSuccess(anyString());
        verify(inativarUsuarioFeederMqSender, times(2)).sendSuccess(anyString());
    }

    @Test
    public void deletarHistoricoUsuarioAcesso_deveDeletarRegistros_quandoDataCadastroUltrapassarDoisMeses() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado("XBRAIN"));
        usuarioAcessoService.deletarHistoricoUsuarioAcesso();
        verify(usuarioAcessoRepository, times(1)).deletarHistoricoUsuarioAcesso();
    }

    @Test
    public void deletarHistoricoUsuarioAcesso_deveLancarException_quandoTentarDeletarHistoricoSemPermissaoXbrain() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado("MSO"));
        assertThatThrownBy(() -> usuarioAcessoService.deletarHistoricoUsuarioAcesso())
            .isInstanceOf(PermissaoException.class);
    }

    @Test
    public void registrarLogout_deveRegistrarOlogoutDoUsuario_quandoDeslogarDoSistema() {
        usuarioAcessoService.registrarLogout(100);

        verify(usuarioAcessoRepository, times(1))
            .save(any(UsuarioAcesso.class));
    }

    @Test
    public void getRegistros_retornaRegistrosOrdenados_quandoExistir() {
        var listaUsuarioAcesso = List.of(umUsuarioAcesso(1, 14, 29),
            umUsuarioAcesso(1, 14, 29),
            umUsuarioAcesso(3, 13, 29),
            umUsuarioAcesso(2, 16, 28));

        when(usuarioAcessoRepository.findAll(umUsuarioAcessoFiltros().toPredicate()))
            .thenReturn(listaUsuarioAcesso);

        assertThat(usuarioAcessoService.getRegistros(umUsuarioAcessoFiltros()))
            .extracting("id", "dataHora")
            .containsExactly(
                Tuple.tuple(1, "29/01/2020 14:00:00"),
                Tuple.tuple(3, "29/01/2020 13:00:00"),
                Tuple.tuple(2, "28/01/2020 16:00:00")
            );
    }

    @Test
    public void getCsv_retornaStringCsvOrdemReversa_quandoExistirRegistros() {
        var listaUsuarioAcesso = List.of(umUsuarioAcesso(1, 14, 29),
                umUsuarioAcesso(2, 13, 29),
                umUsuarioAcesso(3, 16, 28)).stream()
            .map(UsuarioAcessoResponse::of)
            .collect(Collectors.toList());

        assertThat(usuarioAcessoService.getCsv(listaUsuarioAcesso))
            .isEqualTo("ID;NOME;CPF;E-MAIL;DATA;\n"
                + "1;;;;29/01/2020 14:00:00\n"
                + "2;;;;29/01/2020 13:00:00\n"
                + "3;;;;28/01/2020 16:00:00");
    }

    @Test
    public void getTotalUsuariosLogadosPorPeriodoByFiltros_totalUsuariosLogadosDeAcordoComFiltro_quandoExistirUsuariosLogados() {
        when(usuarioRepository.findAll(any(Predicate.class))).thenReturn(List.of(new Usuario(101)));
        when(notificacaoUsuarioAcessoService.countUsuariosLogadosPorPeriodo(any())).thenReturn(umUsuarioLogadoResponse());

        var response = usuarioAcessoService.getTotalUsuariosLogadosPorPeriodoByFiltros(umUsuarioLogadoRequest());

        assertThat(response)
            .extracting("dataInicial", "dataFinal", "totalUsuariosLogados")
            .containsExactly(
                tuple("2020-12-01T10:00:00.000Z", "2020-12-01T10:59:59.999Z", 10),
                tuple("2020-12-01T11:00:00.000Z", "2020-12-01T11:42:39.999Z", 3)
            );

        verify(notificacaoUsuarioAcessoService, times(1))
            .countUsuariosLogadosPorPeriodo(any(UsuarioLogadoRequest.class));
    }

    @Test
    public void getTotalUsuariosLogadosPorPeriodoByFiltros_deveRetornarPeriodosComTotalUsuariosZero_quandoNaoExistirUsuario() {
        when(usuarioRepository.findAll(any(Predicate.class))).thenReturn(List.of());

        var response = usuarioAcessoService.getTotalUsuariosLogadosPorPeriodoByFiltros(umUsuarioLogadoRequest());

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
        when(notificacaoUsuarioAcessoService.getUsuariosLogadosAtualPorIds(eq(List.of(101, 201)))).thenReturn(List.of(101));

        var response = usuarioAcessoService.getUsuariosLogadosAtualPorIds(
            UsuarioLogadoRequest.builder()
                .organizacaoId(6)
                .cargos(List.of(CodigoCargo.BACKOFFICE_OPERADOR_TRATAMENTO))
                .build());

        assertThat(response).containsExactly(101);

        verify(notificacaoUsuarioAcessoService, times(1))
            .getUsuariosLogadosAtualPorIds(eq(List.of(101, 201)));
        verify(usuarioRepository, times(1))
            .findAll(eq(new UsuarioPredicate()
                .comOrganizacaoEmpresaId(6)
                .comCodigosCargos(List.of(CodigoCargo.BACKOFFICE_OPERADOR_TRATAMENTO))
                .isAtivo(Eboolean.V)
                .build()));
    }

    private UsuarioAcesso umUsuarioAcesso(Integer id, Integer hora, Integer dia) {
        return UsuarioAcesso.builder()
            .id(id)
            .dataCadastro(LocalDateTime.of(2020, 01, dia, hora, 00))
            .usuario(Usuario.builder().id(id).build())
            .build();
    }

    private UsuarioAcessoFiltros umUsuarioAcessoFiltros() {
        return UsuarioAcessoFiltros.builder()
            .dataInicio(LocalDate.now().minusDays(1))
            .dataFim(LocalDate.now())
            .dataInicial(LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIN))
            .dataFinal(LocalDateTime.of(LocalDate.now(), LocalTime.MAX))
            .tipo(ETipo.LOGIN)
            .build();
    }

    private UsuarioAutenticado umUsuarioAutenticado(String nivelCodigo) {
        return UsuarioAutenticado.builder()
            .id(100)
            .nivelCodigo(nivelCodigo)
            .build();
    }

    private List<PaLogadoDto> umUsuarioLogadoResponse() {
        return List.of(
            PaLogadoDto.builder()
                .dataInicial("2020-12-01T10:00:00.000Z")
                .dataFinal("2020-12-01T10:59:59.999Z")
                .totalUsuariosLogados(10)
                .build(),
            PaLogadoDto.builder()
                .dataInicial("2020-12-01T11:00:00.000Z")
                .dataFinal("2020-12-01T11:42:39.999Z")
                .totalUsuariosLogados(3)
                .build());
    }

    private UsuarioLogadoRequest umUsuarioLogadoRequest() {
        return UsuarioLogadoRequest.builder()
            .cargos(List.of(CodigoCargo.BACKOFFICE_OPERADOR_TRATAMENTO, CodigoCargo.BACKOFFICE_ANALISTA_TRATAMENTO))
            .organizacaoId(6)
            .periodos(List.of(PaLogadoDto.builder()
                    .dataInicial("2020-12-01T10:00:00.000Z")
                    .dataFinal("2020-12-01T10:59:59.999Z")
                    .build(),
                PaLogadoDto.builder()
                    .dataInicial("2020-12-01T11:00:00.000Z")
                    .dataFinal("2020-12-01T11:42:39.999Z")
                    .build()))
            .build();
    }
}
