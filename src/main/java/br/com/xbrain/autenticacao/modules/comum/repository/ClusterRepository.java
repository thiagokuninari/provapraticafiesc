package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Cluster;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ClusterRepository extends PagingAndSortingRepository<Cluster, Integer>, ClusterRepositoryCustom {

    @Cacheable("clusterFindBySituacaoAndGrupoId")
    Iterable<Cluster> findBySituacaoAndGrupoId(ESituacao situacao, Integer grupoId, Sort sort);

    @Cacheable("clusterFindBySituacao")
    List<Cluster> findBySituacao(ESituacao situacao, Sort sort);

}
