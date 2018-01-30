package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto;
import br.com.xbrain.autenticacao.modules.comum.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/clusters")
public class ClusterController {

    @Autowired
    private ClusterService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<ClusterDto> getAtivosPorGrupo(Integer grupoId) {
        if (grupoId != null) {
            return service.getAllByGrupoId(grupoId);
        } else {
            return service.getAllAtivo();
        }
    }
}