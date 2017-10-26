package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.regex.Pattern;

public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter implements
        JwtAccessTokenConverterConfigurer {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        OAuth2AccessToken enhancedToken = super.enhance(accessToken,authentication);

        if (authentication.getUserAuthentication() != null) {
            User user = (User) authentication.getUserAuthentication().getPrincipal();

            usuarioService
                    .getRepository()
                    .findComplete(new Integer(user.getUsername().split(Pattern.quote("-"))[0]))
                    .ifPresent(u -> setAdditionalInformation(enhancedToken, u, user));
        }

        return enhancedToken;
    }

    public void setAdditionalInformation(OAuth2AccessToken token, Usuario usuario, User user) {
        token.getAdditionalInformation().put("login", user.getUsername());
        token.getAdditionalInformation().put("permissoes",
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray());
        token.getAdditionalInformation().put("nome", usuario.getNome());
        token.getAdditionalInformation().put("alterarSenha", usuario.getAlterarSenha());
        token.getAdditionalInformation().put("nivel", usuario.getCargo().getNivel().getNome());
        token.getAdditionalInformation().put("departamento", usuario.getDepartamento().getNome());
        token.getAdditionalInformation().put("cargo", usuario.getCargo().getNome());
    }

    @Override
    public void configure(JwtAccessTokenConverter converter) {
        converter.setAccessTokenConverter(this);
    }
}
