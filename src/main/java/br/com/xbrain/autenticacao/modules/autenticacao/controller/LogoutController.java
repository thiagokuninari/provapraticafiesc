package br.com.xbrain.autenticacao.modules.autenticacao.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_DESLOGAR_USUARIO;

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
        if (autenticacaoService.getUsuarioAutenticado().hasPermissao(AUT_DESLOGAR_USUARIO)) {
            autenticacaoService.logout(usuarioId);
        } else {
            throw new PermissaoException();
        }
    }

    @GetMapping("/todos-usuarios")
    public void logoutAll() {
        if (autenticacaoService.getUsuarioAutenticado().hasPermissao(AUT_DESLOGAR_USUARIO)) {
            autenticacaoService.logoutAllUsers();
        } else {
            throw new PermissaoException();
        }
    }
}