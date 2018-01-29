package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface SubClusterRepositoryCustom {

    List<SubCluster> findAllByClusterId(Integer clusterId, Predicate predicate);

}
