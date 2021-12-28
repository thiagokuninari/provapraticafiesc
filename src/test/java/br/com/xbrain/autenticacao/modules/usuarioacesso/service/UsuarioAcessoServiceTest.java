package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.InativarColaboradorMqSender;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Java6Assertions.tuple;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
public class UsuarioAcessoServiceTest {

    @Autowired
    private UsuarioAcessoService usuarioAcessoService;
    @MockBean
    private UsuarioAcessoRepository usuarioAcessoRepository;
    @MockBean
    private UsuarioRepository usuarioRepository;
    @MockBean
    private UsuarioHistoricoService usuarioHistoricoService;
    @MockBean
    private InativarColaboradorMqSender inativarColaboradorMqSender;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private NotificacaoUsuarioAcessoService notificacaoUsuarioAcessoService;

    @Before
    public void setup() {
        when(usuarioAcessoRepository.findAllUltimoAcessoUsuarios())
            .thenReturn(List.of(
                new UsuarioAcesso(
                    LocalDateTime.now().minusDays(45), 102, "RENATO@XBRAIN.COM.BR"),
                new UsuarioAcesso(
                    LocalDateTime.now().minusDays(33), 103, "MARIA@XBRAIN.COM.BR"),
                new UsuarioAcesso(
                    LocalDateTime.now().minusDays(33), 104, "JOANA@XBRAIN.COM.BR"),
                new UsuarioAcesso(
                    LocalDateTime.now().minusDays(45), 105, null),
                new UsuarioAcesso(
                    LocalDateTime.now().minusDays(10), 106, "CARLOS@XBRAIN.COM.BR")));
    }

    @Test
    public void registrarAcesso_deveRegistrarAcesso_quandoUsuarioEfetuarLogin() {
        usuarioAcessoService.registrarAcesso(100);

        verify(usuarioAcessoRepository, times(1))
                .save(any(UsuarioAcesso.class));
    }

    @Test
    public void inativarUsuariosSemAcesso_deveInativarUsuarios_quandoNaoEfetuarLoginPorTrintaEDoisDias() {
        usuarioAcessoService.inativarUsuariosSemAcesso("TESTE");

        verify(usuarioRepository, times(3)).atualizarParaSituacaoInativo(anyInt());
        verify(usuarioHistoricoService, times(3)).gerarHistoricoInativacao(any(Usuario.class), any(String.class));
        verify(inativarColaboradorMqSender, times(3)).sendSuccess(anyString());
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

    private UsuarioAcesso umUsuarioAcesso(Integer id, Integer hora, Integer dia) {
        return UsuarioAcesso.builder()
            .id(id)
            .dataCadastro(LocalDateTime.of(2020,01, dia, hora,00))
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
