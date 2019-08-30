package br.com.xbrain.autenticacao.modules.usuarioacesso.controller;

import br.com.xbrain.autenticacao.modules.usuarioacesso.service.UsuarioAcessoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/usuario-acesso")
public class UsuarioAcessoController {

    @Autowired
    private UsuarioAcessoService usuarioAcessoService;

    @GetMapping("/inativar")
    @ResponseStatus(HttpStatus.OK)
    public void inativarUsuariosSemAcesso() {
        usuarioAcessoService.inativarUsuariosSemAcesso();
    }

    @DeleteMapping("historico")
    public void deletarHistoricoUsuarioAcesso() {
        usuarioAcessoService.deletarHistoricoUsuarioAcesso();
    }
}
