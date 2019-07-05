package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Cluster;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.model.QCluster.cluster;

public class ClusterRepositoryImpl extends CustomRepository<Cluster> implements ClusterRepositoryCustom {

    @Override
    public List<Cluster> findAllByGrupoId(Integer grupoId, Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(cluster)
                .from(cluster)
                .where(cluster.situacao.eq(ESituacao.A)
                        .and(cluster.grupo.id.eq(grupoId))
                        .and(predicate))
                .orderBy(cluster.nome.asc())
                .fetch();
    }

    @Override
    public List<Cluster> findById(Integer clusterId) {
        return new JPAQueryFactory(entityManager)
            .select(cluster)
            .from(cluster)
            .where(cluster.id.eq(clusterId))
            .fetch();
    }
}
