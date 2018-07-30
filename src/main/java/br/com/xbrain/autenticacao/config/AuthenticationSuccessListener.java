package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.User;

//@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private AutenticacaoService autenticacaoService;
    
    @Autowired
    private UsuarioHistoricoService usuarioHistoricoService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        deslogaUsuariosAutenticadosComOMesmoLogin(event);
        registrarUltimoAcesso(event);
    }

    private void deslogaUsuariosAutenticadosComOMesmoLogin(AuthenticationSuccessEvent event) {
        if (event.getSource() instanceof UsernamePasswordAuthenticationToken && !autenticacaoService.isEmulacao()) {
            String login = ((User) ((UsernamePasswordAuthenticationToken)
                    event.getSource()).getPrincipal()).getUsername();
            autenticacaoService.logout(login);
        }
    }    

    private void registrarUltimoAcesso(AuthenticationSuccessEvent event) {
        if (event.getAuthentication().isAuthenticated()) {
            String login = ((User)event.getAuthentication().getPrincipal()).getUsername();
            Integer usuarioId =  new Integer(login.split("-")[0]);
            usuarioHistoricoService.registrarHistoricoUltimoAcessoAsync(usuarioId);    
        }
        
    }
}
