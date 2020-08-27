package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.InativarColaboradorMqSender;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.PaLogadoResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.enums.ETipo;
import br.com.xbrain.autenticacao.modules.usuarioacesso.filtros.UsuarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import br.com.xbrain.autenticacao.modules.usuarioacesso.repository.UsuarioAcessoRepository;
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
import static org.mockito.Mockito.*;

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

    @Before
    public void setup() {
        when(usuarioAcessoRepository.findAllUltimoAcessoUsuarios())
            .thenReturn(List.of(
                new UsuarioAcesso(
                    LocalDateTime.now().minusDays(45), 102, "RENATO@XBRAIN.COM.BR"),
                new UsuarioAcesso(
                    LocalDateTime.now().minusDays(33), 103, "MARIA@XBRAIN.COM.BR"),
                new UsuarioAcesso(
                    LocalDateTime.now().minusDays(32), 104, "JOANA@XBRAIN.COM.BR"),
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
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado("XBRAIN"));
        usuarioAcessoService.inativarUsuariosSemAcesso();

        verify(usuarioRepository, times(4)).atualizarParaSituacaoInativo(anyInt());
        verify(usuarioHistoricoService, times(4)).gerarHistoricoInativacao(any(Usuario.class));
        verify(inativarColaboradorMqSender, times(3)).sendSuccess(anyString());
    }

    @Test
    public void inativarUsuariosSemAcesso_deveInativarUsuarios_aa() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado("MSO"));

        assertThatThrownBy(() -> usuarioAcessoService.inativarUsuariosSemAcesso())
            .isInstanceOf(PermissaoException.class);
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
    public void getAllLoginByFiltros_loginsDeAcordoComFiltro_quandoExistirLogins() {
        when(usuarioAcessoRepository.getAllLoginByFiltros(any())).thenReturn(getLogadoResponse());

        var filtros = new UsuarioAcessoFiltros();
        filtros.setDataInicial(LocalDateTime.now());
        filtros.setDataFinal(LocalDateTime.now());
        var response = usuarioAcessoService.getAllLoginByFiltros(filtros);

        assertThat(response)
            .hasSize(3)
            .extracting("hora", "paLogados")
            .containsExactly(
                tuple(8, 20L),
                tuple(9, 10L),
                tuple(10, 1L)
            );
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

    private List<PaLogadoResponse> getLogadoResponse() {
        return List.of(
            new PaLogadoResponse(8, 20L),
            new PaLogadoResponse(9, 10L),
            new PaLogadoResponse(10, 1L)
        );
    }
}