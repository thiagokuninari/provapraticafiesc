package br.com.xbrain.autenticacao.modules.usuarioacesso.service;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.InativarColaboradorMqSender;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import br.com.xbrain.autenticacao.modules.usuarioacesso.repository.UsuarioAcessoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.*;

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

//    @Before
//    public void setup() {
//        when(usuarioAcessoRepository.findAllUltimoAcessoUsuarios())
//                .thenReturn(List.of(
//                        new UsuarioAcesso(1, 100, "JOAO@XBRAIN.COM.BR"),
//                        new UsuarioAcesso(2, 101, "CARLOS@XBRAIN.COM.BR"),
//                        new UsuarioAcesso(3, 102, null)));
//    }

    @Test
    public void registrarAcesso_deveRegistrarAcesso_quandoUsuarioEfetuarLogin() {
        usuarioAcessoService.registrarAcesso(100);

        verify(usuarioAcessoRepository, times(1))
                .save(any(UsuarioAcesso.class));
    }

    @Test
    public void inativarUsuariosSemAcesso_deveInativarUsuarios_quandoNaoEfetuarLoginPorTrintaEDoisDiasEPossuirEmailCadastrado() {
        usuarioAcessoService.inativarUsuariosSemAcesso();

        verify(usuarioRepository, times(3)).atualizarParaSituacaoInativo(anyInt());
        verify(usuarioHistoricoService, times(3)).gerarHistoricoInativacao(any(Usuario.class));
        verify(inativarColaboradorMqSender, times(2)).sendSuccess(anyString());
    }
}