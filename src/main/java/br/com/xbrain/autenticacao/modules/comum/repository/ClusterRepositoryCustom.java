package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.dto.ClusterDto;
import br.com.xbrain.autenticacao.modules.comum.model.Cluster;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface ClusterRepositoryCustom {

    List<Cluster> findAllByGrupoId(Integer grupoId, Predicate predicate);

    List<Cluster> findById(Integer clusterId);

}
