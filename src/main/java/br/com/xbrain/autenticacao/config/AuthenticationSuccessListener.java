package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private UsuarioHistoricoService usuarioHistoricoService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        deslogaUsuariosAutenticadosComOMesmoLogin(event);
        //registrarUltimoAcesso(event); TODO foi desativado e será refeito conforme task #13110
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

    public void registrarUltimoAcesso(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            String login = ((User) authentication.getPrincipal()).getUsername();
            Integer usuarioId = Integer.valueOf(login.split("-")[0]);
            usuarioHistoricoService.gerarHistoricoUltimoAcessoDoUsuario(usuarioId);
        }
    }
}
