package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao;
import br.com.xbrain.autenticacao.modules.usuario.service.SupervisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "api/supervisor")
public class SupervisorController {

    @Autowired
    private SupervisorService service;

    @GetMapping("/assistentes-vendedores/{id}")
    public List<UsuarioResponse> getCargosDescendentesEVendedores(@PathVariable Integer id,
                                                           @RequestParam(required = false) Integer equipeId) {
        return service.getCargosDescendentesEVendedoresD2dDoSupervisor(id, equipeId);
    }

    @GetMapping("/por-area-atuacao/{areaAtuacao}/{id}")
    public List<UsuarioResponse> getPorAreaAtuacao(@PathVariable AreaAtuacao areaAtuacao,
                                                   @PathVariable Integer id) {
        return service.getSupervisoresPorAreaAtuacao(areaAtuacao, Collections.singletonList(id));
    }

    @GetMapping("/subcluster/usuario/{id}")
    public List<UsuarioNomeResponse> getSupervisoresDoSubclusterDoUsuario(@PathVariable Integer id) {
        return service.getSupervisoresDoSubclusterDoUsuario(id);
    }

}
