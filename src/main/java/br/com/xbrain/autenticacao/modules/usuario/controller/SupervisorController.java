package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.service.SupervisorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/supervisor")
@RequiredArgsConstructor
public class SupervisorController {

    private final SupervisorService service;

    @GetMapping("/assistentes-vendedores/{id}")
    public List<UsuarioResponse> getCargosDescendentesEVendedores(@PathVariable Integer id,
                                                           @RequestParam(required = false) Integer subCanalId,
                                                           @RequestParam(required = false) Integer equipeId) {
        return service.getCargosDescendentesEVendedoresD2dDoSupervisor(id, equipeId, subCanalId);
    }

    @GetMapping("/por-area-atuacao/{areaAtuacao}/{id}")
    public List<UsuarioResponse> getPorAreaAtuacao(@PathVariable AreaAtuacao areaAtuacao, @PathVariable Integer id) {
        return service.getSupervisoresPorAreaAtuacao(areaAtuacao, Collections.singletonList(id));
    }

    @GetMapping("/lideres-por-area-atuacao/{areaAtuacao}/{id}")
    public List<UsuarioResponse> getLideresPorAreaAtuacao(@PathVariable AreaAtuacao areaAtuacao, @PathVariable Integer id) {
        return service.getLideresPorAreaAtuacao(areaAtuacao, Collections.singletonList(id));
    }

    @GetMapping("/subcluster/usuario/{id}/canal/{canal}")
    public List<UsuarioNomeResponse> getSupervisoresDoSubclusterDoUsuarioPeloCanal(@PathVariable Integer id,
                                                                                   @PathVariable ECanal canal) {
        return service.getSupervisoresDoSubclusterDoUsuarioPeloCanal(id, canal);
    }
}
