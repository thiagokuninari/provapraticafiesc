package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.User;

//@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        deslogaUsuariosAutenticadosComOMesmoLogin(event);
    }

    private void deslogaUsuariosAutenticadosComOMesmoLogin(AuthenticationSuccessEvent event) {
        if (event.getSource() instanceof UsernamePasswordAuthenticationToken && !autenticacaoService.isEmulacao()) {
            String login = ((User) ((UsernamePasswordAuthenticationToken)
                    event.getSource()).getPrincipal()).getUsername();
            autenticacaoService.logout(login);
        }
    }
}
