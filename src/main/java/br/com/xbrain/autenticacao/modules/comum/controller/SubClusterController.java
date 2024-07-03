package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SubClusterDto;
import br.com.xbrain.autenticacao.modules.comum.service.SubClusterService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/subclusters")
@RequiredArgsConstructor
public class SubClusterController {

    private final SubClusterService service;

    @GetMapping
    public List<SubClusterDto> getAtivosPorCluster(@RequestParam(required = false) Integer clusterId) {
        if (!ObjectUtils.isEmpty(clusterId)) {
            return service.getAllByClusterId(clusterId);
        } else {
            return service.getAllAtivos();
        }
    }

    @GetMapping("/cluster/{clusterId}/usuario/{usuarioId}")
    public List<SubClusterDto> getAllByClusterIdAndUsuarioId(@PathVariable Integer clusterId, @PathVariable Integer usuarioId) {
        return service.getAllByClusterIdAndUsuarioId(clusterId, usuarioId);
    }

    @GetMapping("/{subClusterId}")
    public SubClusterDto getById(@PathVariable Integer subClusterId) {
        return service.getById(subClusterId);
    }

    @GetMapping("clusters")
    public List<SubClusterDto> getAtivosPorClusters(@RequestParam(required = false) List<Integer> clustersId) {
        if (!ObjectUtils.isEmpty(clustersId)) {
            return service.getAllByClustersId(clustersId);
        } else {
            return service.getAllAtivos();
        }
    }

    @GetMapping("todos")
    public List<SubClusterDto> getAllSubclusters() {
        return service.getAll();
    }

    @GetMapping("usuario-autenticado")
    public List<SubClusterDto> getAllSubclustersByUsuarioAutenticado() {
        return service.getAllSubclustersByUsuarioAutenticado();
    }
}
