package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Cluster;
import br.com.xbrain.autenticacao.modules.comum.repository.ClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/clusters")
public class ClusterController {

    @Autowired
    private ClusterRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public List<ClusterDto> getAtivosPorGrupo(Integer grupoId) {
        if (grupoId != null) {
            List<Cluster> clusterList =
                    (List<Cluster>) repository.findBySituacaoAndGrupoId(ESituacao.A, grupoId, new Sort("nome"));

            return getClusterDtoList(clusterList);
        } else {
            List<Cluster> clusterList = (List<Cluster>) repository.findBySituacao(ESituacao.A, new Sort("nome"));

            return getClusterDtoList(clusterList);
        }
    }

    private List<ClusterDto> getClusterDtoList(List<Cluster> clusterList) {
        return clusterList
                .stream()
                .map(c -> new ClusterDto(c.getId(), c.getNome()))
                .collect(Collectors.toList());
    }
}