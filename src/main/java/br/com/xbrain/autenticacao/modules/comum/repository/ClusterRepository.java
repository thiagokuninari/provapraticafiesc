package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Cluster;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClusterRepository extends PagingAndSortingRepository<Cluster, Integer> {

    @Cacheable("clusterFindBySituacaoAndGrupoId")
    Iterable<Cluster> findBySituacaoAndGrupoId(ESituacao situacao, Integer grupoId, Sort sort);

    @Cacheable("clusterFindBySituacao")
    Iterable<Cluster> findBySituacao(ESituacao situacao, Sort sort);
}
