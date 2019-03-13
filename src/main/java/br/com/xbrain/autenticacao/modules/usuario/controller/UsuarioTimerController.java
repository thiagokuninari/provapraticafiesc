package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/disparar-timer-inativar-usuarios")
public class UsuarioTimerController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    public String inativarUsuariosSemAcesso() {
        service.inativarUsuariosSemAcesso();
        return "timer disparado";
    }
}
