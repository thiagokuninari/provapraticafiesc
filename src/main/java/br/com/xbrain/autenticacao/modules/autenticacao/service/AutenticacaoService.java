package br.com.xbrain.autenticacao.modules.autenticacao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.repository.OAuthAccessTokenRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

@Service
public class AutenticacaoService {

    public static final String HEADER_USUARIO_EMULADOR = "X-Usuario-Emulador";

    @Autowired
    private OAuthAccessTokenRepository tokenRepository;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public static boolean hasAuthentication() {
        OAuth2Authentication authentication = getAuthentication();
        return authentication != null && authentication.getUserAuthentication() != null;
    }

    public String getLoginUsuario() {
        return getAuthentication().getName();
    }

    public Integer getUsuarioId() {
        return Integer.parseInt(getAuthentication().getName().split(Pattern.quote("-"))[0]);
    }

    public UsuarioAutenticado getUsuarioAutenticado() {
        return loadUsuarioDataBase(getAuthentication());
    }

    private UsuarioAutenticado loadUsuarioDataBase(Authentication authentication) {
        LinkedHashMap details = (LinkedHashMap)
                ((OAuth2Authentication) authentication).getUserAuthentication().getDetails();
        UsuarioAutenticado usuarioAutenticado = null;

        if (details.get("usuarioAutenticado") == null) {
            Usuario usuario = usuarioRepository.findComplete(getUsuarioId()).get();
            usuario.forceLoad();
            usuarioAutenticado = new UsuarioAutenticado(usuario, authentication.getAuthorities());

            details.put("usuarioAutenticado", usuarioAutenticado);
        }
        return usuarioAutenticado;
    }

    @Transactional
    public void logout(String login) {
        tokenRepository.deleteTokenByUsername(login);
    }

    @Transactional
    public void logout(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findOne(usuarioId);
        tokenRepository.deleteTokenByUsername(usuario.getLogin());
    }

    public static OAuth2Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication instanceof OAuth2Authentication ? (OAuth2Authentication) authentication : null;
    }

    public boolean isEmulacao() {
        return request.getAttribute("emulacao") != null;
    }

    public static Integer getUsuarioEmuladorId(HttpServletRequest request) {
        if (request.getHeader(HEADER_USUARIO_EMULADOR) != null) {
            return Integer.parseInt(request.getHeader(HEADER_USUARIO_EMULADOR));
        }
        return null;
    }
}