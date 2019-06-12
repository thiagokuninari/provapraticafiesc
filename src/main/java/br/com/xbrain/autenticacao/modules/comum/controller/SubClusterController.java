package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SubClusterDto;
import br.com.xbrain.autenticacao.modules.comum.service.SubClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/subclusters")
public class SubClusterController {

    @Autowired
    private SubClusterService service;

    @GetMapping
    public List<SubClusterDto> getAtivosPorCluster(@RequestParam(required = false) Integer clusterId) {
        if (!ObjectUtils.isEmpty(clusterId)) {
            return service.getAllByClusterId(clusterId);
        } else {
            return service.getAllAtivos();
        }
    }

    @GetMapping("/usuario")
    public List<SubClusterDto> getAllByClusterIdAndUsuarioId(@RequestParam Integer clusterId, @RequestParam Integer usuarioId) {
        return service.getAllByClusterIdAndUsuarioId(clusterId, usuarioId);
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

}