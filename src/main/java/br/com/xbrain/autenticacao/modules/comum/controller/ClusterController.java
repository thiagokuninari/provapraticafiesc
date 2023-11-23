package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto;
import br.com.xbrain.autenticacao.modules.comum.service.ClusterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/clusters")
@RequiredArgsConstructor
public class ClusterController {

    private final ClusterService service;

    @GetMapping
    public List<ClusterDto> getAtivosPorGrupo(@RequestParam(required = false) Integer grupoId) {
        if (grupoId != null) {
            return service.getAllByGrupoId(grupoId);
        } else {
            return service.getAllAtivo();
        }
    }

    @GetMapping("comunicados")
    public List<ClusterDto> getAtivosParaComunicados(@RequestParam Integer grupoId) {
        return service.getAtivosParaComunicados(grupoId);
    }

    @GetMapping("/grupo/{grupoId}/usuario/{usuarioId}")
    public List<ClusterDto> getAllByGrupoIdAndUsuarioId(@PathVariable Integer grupoId, @PathVariable Integer usuarioId) {
        return service.getAllByGrupoIdAndUsuarioId(grupoId, usuarioId);
    }

    @GetMapping("/{clusterId}")
    public ClusterDto findById(@PathVariable Integer clusterId) {
        return service.findById(clusterId);
    }
}
