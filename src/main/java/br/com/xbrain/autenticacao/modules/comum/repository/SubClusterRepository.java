package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SubClusterRepository extends PagingAndSortingRepository<SubCluster, Integer> {

    @Cacheable("subclusterFindBySituacaoAndClusterId")
    Iterable<SubCluster> findBySituacaoAndClusterId(ESituacao situacao, Integer clusterId, Sort sort);

    @Cacheable("subclusterFindBySituacao")
    Iterable<SubCluster> findBySituacao(ESituacao situacao, Sort sort);
}
