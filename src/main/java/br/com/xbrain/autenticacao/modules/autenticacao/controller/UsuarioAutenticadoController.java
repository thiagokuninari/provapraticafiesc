package br.com.xbrain.autenticacao.modules.autenticacao.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "api/usuario-autenticado")
public class UsuarioAutenticadoController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public UsuarioAutenticado getUsuario(Principal principal) {
        return new UsuarioAutenticado(
                usuarioService.findById(getUsuarioId(principal)));
    }

    @GetMapping("/{id}")
    public UsuarioAutenticado getUsuarioById(@PathVariable("id") int id) {
        return new UsuarioAutenticado(
                usuarioService.findById(id));
    }

    @GetMapping(value = "/{id}/cidades")
    public List<CidadeResponse> getCidadesByUsuario(@PathVariable("id") int id) {
        return usuarioService.findCidadesByUsuario(id);
    }

    private Integer getUsuarioId(Principal principal) {
        return Integer.parseInt(principal.getName().split(Pattern.quote("-"))[0]);
    }
}
