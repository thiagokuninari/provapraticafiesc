package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.model.QSubCluster.subCluster;

public class SubClusterRepositoryImpl extends CustomRepository<SubCluster> implements SubClusterRepositoryCustom {

    @Override
    public List<SubCluster> findAllByClusterId(Integer clusterId, Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(subCluster)
                .from(subCluster)
                .where(subCluster.situacao.eq(ESituacao.A)
                        .and(subCluster.cluster.id.eq(clusterId))
                        .and(predicate))
                .orderBy(subCluster.nome.asc())
                .fetch();
    }

}
