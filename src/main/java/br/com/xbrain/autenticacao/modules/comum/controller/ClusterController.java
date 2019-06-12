package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto;
import br.com.xbrain.autenticacao.modules.comum.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/clusters")
public class ClusterController {

    @Autowired
    private ClusterService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<ClusterDto> getAtivosPorGrupo(@RequestParam(required = false) Integer grupoId) {
        if (grupoId != null) {
            return service.getAllByGrupoId(grupoId);
        } else {
            return service.getAllAtivo();
        }
    }

    @GetMapping("/usuario")
    public List<ClusterDto> getAllByGrupoIdAndUsuarioId(@RequestParam Integer grupoId, @RequestParam Integer usuarioId) {
        return service.getAllByGrupoIdAndUsuarioId(grupoId, usuarioId);
    }
}