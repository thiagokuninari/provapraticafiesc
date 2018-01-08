package br.com.xbrain.autenticacao.modules.permissao.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.permissao.repository.OAuthAccessTokenRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

@Service
@SuppressWarnings("unchecked")
public class AutenticacaoService {

    public static final String HEADER_USUARIO_EMULADOR = "X-Usuario-Emulador";

    @Autowired
    private OAuthAccessTokenRepository tokenRepository;

    @Autowired
    private UsuarioService usuarioService;

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
            Usuario usuario = usuarioService.getRepository().findComplete(getUsuarioId()).get();
            usuarioAutenticado = new UsuarioAutenticado(usuario, authentication.getAuthorities());

            details.put("usuarioAutenticado", usuarioAutenticado);
        }
        return usuarioAutenticado;
    }

    @Transactional
    public void logout(String login) {
        tokenRepository.deleteTokenByUsername(login);
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}