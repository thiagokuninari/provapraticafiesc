package br.com.xbrain.autenticacao.modules.permissao.logout;

import br.com.xbrain.autenticacao.modules.permissao.service.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/logout")
public class LogoutController {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @RequestMapping(method = RequestMethod.GET)
    public void logout() {
        autenticacaoService.logout(autenticacaoService.getLoginUsuario());
    }
}