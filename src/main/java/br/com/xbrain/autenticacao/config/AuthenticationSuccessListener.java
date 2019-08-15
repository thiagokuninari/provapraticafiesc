package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import br.com.xbrain.autenticacao.modules.usuarioacesso.service.UsuarioAcessoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private UsuarioAcessoService usuarioAcessoService;
    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        deslogaUsuariosAutenticadosComOMesmoLogin(event);

        if (!isLogoutEvent(event) && !autenticacaoService.isEmulacao()) {
            registrarAcesso(event);
        }
    }

    private void deslogaUsuariosAutenticadosComOMesmoLogin(AuthenticationSuccessEvent event) {
        if (event.getSource() instanceof UsernamePasswordAuthenticationToken && !autenticacaoService.isEmulacao()) {
            String login = ((User) ((UsernamePasswordAuthenticationToken)
                    event.getSource()).getPrincipal()).getUsername();
            if (autenticacaoService.somenteUmLoginPorUsuario(login)) {
                autenticacaoService.logout(login);
            }
        }
    }

    private void registrarAcesso(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            Integer usuarioId = getUsuarioId(authentication);
            usuarioAcessoService.registrarAcesso(usuarioId);
            usuarioService.atualizarDataUltimoAcesso(usuarioId);
        }
    }

    private boolean isLogoutEvent(AuthenticationSuccessEvent event) {
        return event.getAuthentication()
                .getDetails() instanceof OAuth2AuthenticationDetails;
    }

    private Integer getUsuarioId(Authentication authentication) {
        String login = ((User) authentication.getPrincipal()).getUsername();
        return Integer.valueOf(login.split("-")[0]);
    }
}
