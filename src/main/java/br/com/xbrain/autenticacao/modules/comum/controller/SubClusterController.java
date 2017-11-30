package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SubClusterDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.comum.repository.SubClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/subclusters")
public class SubClusterController {

    @Autowired
    private SubClusterRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public List<SubClusterDto> getAtivosPorCluster(Integer clusterId) {

        if (clusterId != null) {
            List<SubCluster> subClusterList =
                    (List) repository.findBySituacaoAndClusterId(ESituacao.A, clusterId, new Sort("nome"));

            return getSubClusterDtoList(subClusterList);
        } else {
            List<SubCluster> subClusterList = (List) repository.findBySituacao(ESituacao.A, new Sort("nome"));

            return getSubClusterDtoList(subClusterList);
        }
    }

    public List<SubClusterDto> getSubClusterDtoList(List<SubCluster> subClusterList) {
        return subClusterList
                .stream()
                .map(c -> new SubClusterDto(c.getId(), c.getNome()))
                .collect(Collectors.toList());
    }
}