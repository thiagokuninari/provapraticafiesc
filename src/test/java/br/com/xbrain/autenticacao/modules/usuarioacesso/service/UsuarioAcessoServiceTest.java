package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.InativarColaboradorMqSender;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import br.com.xbrain.autenticacao.modules.usuarioacesso.repository.UsuarioAcessoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    public void sddssd() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado("MSO"));
        assertThatThrownBy(() -> usuarioAcessoService.deletarHistoricoUsuarioAcesso())
            .isInstanceOf(PermissaoException.class);
    }

    private UsuarioAutenticado umUsuarioAutenticado(String nivelCodigo) {
        return UsuarioAutenticado.builder()
            .id(100)
            .nivelCodigo(nivelCodigo)
            .build();
    }
}