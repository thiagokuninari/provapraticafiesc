package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.comum.model.QCluster.cluster;
import static br.com.xbrain.autenticacao.modules.comum.model.QGrupo.grupo;
import static br.com.xbrain.autenticacao.modules.comum.model.QRegional.regional;
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

    @Override
    public List<SubCluster> findAllByClustersId(List<Integer> clusterId, Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(subCluster)
                .from(subCluster)
                .where(subCluster.situacao.eq(ESituacao.A)
                        .and(subCluster.cluster.id.in(clusterId))
                        .and(predicate))
                .orderBy(subCluster.nome.asc())
                .fetch();
    }

    @Override
    public List<SubCluster> findAllAtivo(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(subCluster)
                .from(subCluster)
                .where(subCluster.situacao.eq(ESituacao.A)
                        .and(predicate))
                .orderBy(subCluster.nome.asc())
                .fetch();
    }

    @Override
    public Optional<SubCluster> findByIdCompleto(Integer subClusterId) {
        return Optional.ofNullable(new JPAQueryFactory(entityManager)
            .select(subCluster)
            .from(subCluster)
            .innerJoin(subCluster.cluster, cluster).fetchJoin()
            .innerJoin(cluster.grupo, grupo).fetchJoin()
            .innerJoin(grupo.regional, regional).fetchJoin()
            .where(subCluster.id.eq(subClusterId))
            .fetchOne());
    }
}
