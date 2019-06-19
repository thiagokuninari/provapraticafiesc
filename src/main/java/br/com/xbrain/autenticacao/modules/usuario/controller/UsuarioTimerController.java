package br.com.xbrain.autenticacao.modules.usuario.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/public/disparar-timer-inativar-usuarios")
public class UsuarioTimerController {

    @GetMapping
    public String inativarUsuariosSemAcesso() {
        //service.inativarUsuariosSemAcesso(); TODO foi desativado e será refeito conforme task #13110
        return "timer que inativa usuários após 32 dias sem acesso ao sistema executado.";
    }
}
