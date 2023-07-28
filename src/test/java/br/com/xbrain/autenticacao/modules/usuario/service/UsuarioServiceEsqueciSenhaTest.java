package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
import br.com.xbrain.autenticacao.modules.permissao.exception.ExceedMaxTriesResetPassException;
import br.com.xbrain.autenticacao.modules.permissao.exception.InvalidTokenResetPassException;
import br.com.xbrain.autenticacao.modules.permissao.service.JsonWebTokenService;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import io.jsonwebtoken.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioServiceEsqueciSenhaTest {

    private static final long EXPIRACAO_EM_VINTE_MINUTOS = (long) 1200 * 1000L;
    private static final String ENCODED_KEY = "UVT/z+i0v9lJX36/nej7ug==";

    @InjectMocks
    private UsuarioServiceEsqueciSenha service;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private EmailService emailService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private JsonWebTokenService jsonWebTokenService;
    @Mock
    private NotificacaoService notificacaoService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Captor
    private ArgumentCaptor<String> argumentCaptorSenha;

    @Test
    public void enviarConfirmacaoResetarSenha_deveEnviarConfirmacaoResetarSenha_seSolicitado() {
        var user = new Usuario();
        user.setEmail("teste@xbrain.com.br");
        user.setRecuperarSenhaHash(null);
        user.setRecuperarSenhaTentativa(0);

        when(usuarioRepository.findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(anyString(), any()))
            .thenReturn(Optional.of(user));

        service.enviarConfirmacaoResetarSenha("admin@xbrain.com.br");

        verify(notificacaoService, times(1)).enviarEmailResetSenha(any(), any());
    }

    @Test(expected = ExceedMaxTriesResetPassException.class)
    public void enviarConfirmacaoResetarSenha_deveLancarException_seUltrapassarMaximoTentativaComTokenValido() {
        var hash = createJsonWebTokenResetSenha("teste@xbrain.com.br", 2);
        var usuario = new Usuario();
        usuario.setEmail("teste@xbrain.com.br");
        usuario.setRecuperarSenhaHash(hash);
        usuario.setRecuperarSenhaTentativa(4);

        when(usuarioRepository.findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(anyString(), any()))
             .thenReturn(Optional.of(usuario));

        when(jsonWebTokenService.validateTokenPasswordReset(hash)).thenReturn(getJwsClaims(hash));

        service.enviarConfirmacaoResetarSenha("teste@xbrain.com.br");

        verify(notificacaoService, never()).enviarEmailResetSenha(any(), any());
    }

    @Test
    public void resetarSenha_deveResetarSenha_seSolicitado() {
        var hash = createJsonWebTokenResetSenha(
                "teste@xbrain.com.br",
                1);

        var usuario = new Usuario();
        usuario.setId(1);
        usuario.setEmail("teste@xbrain.com.br");
        usuario.setRecuperarSenhaHash(hash);

        when(jsonWebTokenService.validateTokenPasswordReset(hash)).thenReturn(getJwsClaims(hash));
        when(usuarioRepository.findUsuarioByEmail(Matchers.anyString())).thenReturn(Optional.of(usuario));

        service.notificarCliente(usuario, false, null);
        service.resetarSenha(hash);

        verify(usuarioRepository, times(1)).updateSenha(argumentCaptorSenha.capture(), any(), any());

        assertThat(argumentCaptorSenha.getValue()).isEqualTo(null);
    }

    @Test(expected = InvalidTokenResetPassException.class)
    public void resetarSenha_deveLancarException_seTokenExpirada() {
        var date = new Date(System.currentTimeMillis() - 1200000).getTime();
        var hashExpirada = createJsonWebTokenResetSenha("teste@xbrain.com.br", 1, date);
        var usuario = new Usuario();
        usuario.setId(1);
        usuario.setEmail("teste@xbrain.com.br");
        usuario.setRecuperarSenhaHash(hashExpirada);

        doThrow(InvalidTokenResetPassException.class)
            .when(jsonWebTokenService).validateTokenPasswordReset(hashExpirada);

        service.resetarSenha(hashExpirada);

        verify(usuarioRepository, never()).updateSenha(any(), any(), any());
    }

    @Test(expected = InvalidTokenResetPassException.class)
    public void resetarSenha_deveLancarException_seTokenInvalida() {
        var usuario = new Usuario();
        usuario.setId(1);
        usuario.setEmail("teste@xbrain.com.br");
        usuario.setRecuperarSenhaHash("hashnadahaver");

        doThrow(InvalidTokenResetPassException.class)
            .when(jsonWebTokenService).validateTokenPasswordReset(usuario.getRecuperarSenhaHash());

        service.resetarSenha(usuario.getRecuperarSenhaHash());

        verify(usuarioRepository, never()).updateSenha(any(), any(), any());
    }

    @Test(expected = InvalidTokenResetPassException.class)
    public void resetarSenha_deveLancarException_seHashDiferentee() {
        var hash = createJsonWebTokenResetSenha("teste@xbrain.com.br", 2);
        var usuario = new Usuario();
        usuario.setId(1);
        usuario.setEmail("teste@xbrain.com.br");
        usuario.setRecuperarSenhaHash(hash);

        var hashDiferenteDoUsuario = createJsonWebTokenResetSenha("teste@xbrain.com.br",
            1);

        when(jsonWebTokenService.validateTokenPasswordReset(hashDiferenteDoUsuario))
            .thenReturn(getJwsClaims(hashDiferenteDoUsuario));
        when(usuarioRepository.findUsuarioByEmail(anyString())).thenReturn(Optional.of(usuario));

        service.resetarSenha(hashDiferenteDoUsuario);

        verify(usuarioRepository, never()).updateSenha(any(), any(), any());
    }

    @Test
    public void enviarConfirmacaoResetarSenha_deveResetarComTokenInvalidoEMaiorQueMaxTentativas_seSolicitado() {
        var date = new Date(System.currentTimeMillis() - 1200000).getTime();
        var hashExpirada = createJsonWebTokenResetSenha("teste@xbrain.com.br", 1, date);
        var usuario = new Usuario();
        usuario.setId(1);
        usuario.setRecuperarSenhaTentativa(4);
        usuario.setEmail("teste@xbrain.com.br");
        usuario.setRecuperarSenhaHash(hashExpirada);

        doThrow(ExpiredJwtException.class)
            .when(jsonWebTokenService).validateTokenPasswordReset(hashExpirada);

        when(usuarioRepository.findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(anyString(), any()))
            .thenReturn(Optional.of(usuario));

        service.enviarConfirmacaoResetarSenha("teste@xbrain.com.br");

        verify(notificacaoService, times(1))
            .enviarEmailResetSenha(any(), any());
    }

    private Key getDeserializeKey(String encodedKey) {
        var decodedKey = Base64.getDecoder().decode(encodedKey);
        var key = new SecretKeySpec(decodedKey, SignatureAlgorithm.HS512.getJcaName());
        return key;
    }

    private String createJsonWebTokenResetSenha(String email, Integer id) {
        return Jwts.builder()
            .claim("id", id)
            .claim("email", email)
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRACAO_EM_VINTE_MINUTOS))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .signWith(SignatureAlgorithm.HS256, getDeserializeKey(ENCODED_KEY)).compact();
    }

    private String createJsonWebTokenResetSenha(String email, Integer id, Long exp) {
        return Jwts.builder()
            .claim("id", id)
            .claim("email", email)
            .setExpiration(new Date(exp + EXPIRACAO_EM_VINTE_MINUTOS))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .signWith(SignatureAlgorithm.HS256, getDeserializeKey(ENCODED_KEY)).compact();
    }

    private Jws<Claims> getJwsClaims(String hash) {
        return Jwts.parser().setSigningKey(getDeserializeKey(ENCODED_KEY)).parseClaimsJws(hash);
    }
}
