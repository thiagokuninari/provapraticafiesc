package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHistoricoDto;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/usuario-historico")
public class UsuarioHistoricoController {

    @Autowired
    private UsuarioHistoricoService usuarioHistoricoService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public List<UsuarioHistoricoDto> getHistoricoDoUsuario(@PathVariable("id") Integer usuarioId) {
        return usuarioHistoricoService.getHistoricoDoUsuario(usuarioId);
    }

}
