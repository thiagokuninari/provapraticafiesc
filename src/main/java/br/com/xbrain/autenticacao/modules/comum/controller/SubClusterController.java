package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SubClusterDto;
import br.com.xbrain.autenticacao.modules.comum.service.SubClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/subclusters")
public class SubClusterController {

    @Autowired
    private SubClusterService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<SubClusterDto> getAtivosPorCluster(Integer clusterId) {
        if (clusterId != null) {
            return service.getAllByClusterId(clusterId);
        } else {
            return service.getAllAtivos();
        }
    }

}