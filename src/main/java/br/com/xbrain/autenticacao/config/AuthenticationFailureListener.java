package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent authenticationFailureBadCredentialsEvent) {
        var source = authenticationFailureBadCredentialsEvent.getSource();
        if (source instanceof UsernamePasswordAuthenticationToken) {
            var email = ((UsernamePasswordAuthenticationToken) source).getPrincipal().toString();
            usuarioService.gerarHistoricoTentativasLoginSenhaIncorreta(email);
        }
    }

}
