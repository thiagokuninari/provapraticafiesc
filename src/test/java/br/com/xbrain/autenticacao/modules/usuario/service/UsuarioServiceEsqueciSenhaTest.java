package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
import br.com.xbrain.autenticacao.modules.permissao.exception.ExceedMaxTriesResetPassException;
import br.com.xbrain.autenticacao.modules.permissao.exception.InvalidTokenResetPassException;
import br.com.xbrain.autenticacao.modules.permissao.service.JsonWebTokenService;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class UsuarioServiceEsqueciSenhaTest {

    @Autowired
    private UsuarioServiceEsqueciSenha service;

    @MockBean
    private AutenticacaoService autenticacaoService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JsonWebTokenService jsonWebTokenService;

    @MockBean
    private NotificacaoService notificacaoService;

    @Test
    public void deveEnviarConfirmacaoResetarSenha() throws Exception {
        Usuario user = new Usuario();
        user.setEmail("teste@xbrain.com.br");
        user.setRecuperarSenhaHash(null);
        user.setRecuperarSenhaTentativa(0);
        when(usuarioRepository.findUsuarioByEmail(Matchers.anyString())).thenReturn(Optional.of(user));

        service.enviarConfirmacaoResetarSenha("admin@xbrain.com.br");
    }

    @Test(expected = ExceedMaxTriesResetPassException.class)
    public void deveOcorrerExceptionPorUltrapassarMaximoTentativaComTokenValido() throws Exception {
        String hash = jsonWebTokenService.createJsonWebTokenResetSenha("teste@xbrain.com.br", 2);
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@xbrain.com.br");
        usuario.setRecuperarSenhaHash(hash);
        usuario.setRecuperarSenhaTentativa(4);
        when(usuarioRepository.findUsuarioByEmail(Matchers.anyString())).thenReturn(Optional.of(usuario));
        service.enviarConfirmacaoResetarSenha("teste@xbrain.com.br");
    }

    @Test
    public void deveResetarSenha() throws Exception {
        String hash = jsonWebTokenService.createJsonWebTokenResetSenha(
                "teste@xbrain.com.br",
                1);
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setEmail("teste@xbrain.com.br");
        usuario.setRecuperarSenhaHash(hash);
        when(usuarioRepository.findUsuarioByEmail(Matchers.anyString())).thenReturn(Optional.of(usuario));
        service.notificarCliente(usuario, false, null);
        service.resetarSenha(hash);
    }

    @Test(expected = InvalidTokenResetPassException.class)
    public void deveOcorrerExceptionTokenExpirada() throws Exception {
        Long date = new Date(System.currentTimeMillis() - 1200000).getTime();
        String hashExpirada = jsonWebTokenService.createJsonWebTokenResetSenha("teste@xbrain.com.br", 1, date);
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setEmail("teste@xbrain.com.br");
        usuario.setRecuperarSenhaHash(hashExpirada);
        when(usuarioRepository.findUsuarioByEmail(Matchers.anyString())).thenReturn(Optional.of(usuario));
        service.resetarSenha(hashExpirada);
    }

    @Test(expected = InvalidTokenResetPassException.class)
    public void deveOcorrerExceptionTokenInvalida() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setEmail("teste@xbrain.com.br");
        usuario.setRecuperarSenhaHash("hashnadahaver");
        when(usuarioRepository.findUsuarioByEmail(Matchers.anyString())).thenReturn(Optional.of(usuario));
        service.resetarSenha(usuario.getRecuperarSenhaHash());
    }

    @Test(expected = InvalidTokenResetPassException.class)
    public void deveOcorrerExceptionInvalidTokenResetPassException() throws Exception {
        String hash = jsonWebTokenService.createJsonWebTokenResetSenha("teste@xbrain.com.br", 2);
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setEmail("teste@xbrain.com.br");
        usuario.setRecuperarSenhaHash(hash);
        when(usuarioRepository.findUsuarioByEmail(Matchers.anyString())).thenReturn(Optional.of(usuario));

        String hashDiferenteDoUsuario = jsonWebTokenService.createJsonWebTokenResetSenha("teste@xbrain.com.br",
                1);
        service.resetarSenha(hashDiferenteDoUsuario);
    }

    @Test
    public void deveResetarComTokenInvalidoEMaiorQueMaxTentativas() throws Exception {
        Long date = new Date(System.currentTimeMillis() - 1200000).getTime();
        String hashExpirada = jsonWebTokenService.createJsonWebTokenResetSenha("teste@xbrain.com.br", 1, date);
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setRecuperarSenhaTentativa(4);
        usuario.setEmail("teste@xbrain.com.br");
        usuario.setRecuperarSenhaHash(hashExpirada);
        when(usuarioRepository.findUsuarioByEmail(Matchers.anyString())).thenReturn(Optional.of(usuario));
        service.enviarConfirmacaoResetarSenha("teste@xbrain.com.br");
    }
}
