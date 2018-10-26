package br.com.xbrain.autenticacao.modules.autenticacao.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/logout")
public class LogoutController {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @GetMapping
    public void logout() {
        autenticacaoService.logout(autenticacaoService.getLoginUsuario());
    }

    @GetMapping("/usuario/{usuarioId}")
    public void logout(@PathVariable("usuarioId") int usuarioId) {
        autenticacaoService.logout(usuarioId);
    }

    @GetMapping("/todos-usuarios")
    public void logoutAll() {
        if (autenticacaoService.getUsuarioAutenticado().getNivel().equals(CodigoNivel.XBRAIN.name())) {
            autenticacaoService.logoutAllUsers();
            return;
        }
        throw new PermissaoException();
    }
}