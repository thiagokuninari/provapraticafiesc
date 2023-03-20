package br.com.xbrain.autenticacao.modules.autenticacao.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
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

    @GetMapping("{usuarioId}")
    public void logoutPorId(@PathVariable Integer usuarioId) {
        autenticacaoService.logout(usuarioId);
    }

    @GetMapping("/todos-usuarios")
    public void logoutAll() {
        autenticacaoService.logoutAllUsers();
    }

    @GetMapping("usuario-multiplo/{usuarioId}")
    public void logoutUsuarioMultiplo(@PathVariable Integer usuarioId) {
        autenticacaoService.logoutLoginMultiplo(usuarioId);
    }
}
