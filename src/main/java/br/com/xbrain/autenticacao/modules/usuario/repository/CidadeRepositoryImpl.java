package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.ClusterizacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.comum.model.QCluster.cluster;
import static br.com.xbrain.autenticacao.modules.comum.model.QGrupo.grupo;
import static br.com.xbrain.autenticacao.modules.comum.model.QRegional.regional;
import static br.com.xbrain.autenticacao.modules.comum.model.QSubCluster.subCluster;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;

@SuppressWarnings("PMD.TooManyStaticImports")
public class CidadeRepositoryImpl extends CustomRepository<Cidade> implements CidadeRepositoryCustom {

    @Override
    public Iterable<Cidade> findBySubCluster(Integer subClusterId) {
        return new JPAQueryFactory(entityManager)
                .select(cidade)
                .from(cidade)
                .leftJoin(cidade.uf).fetchJoin()
                .leftJoin(cidade.subCluster, subCluster).fetchJoin()
                .leftJoin(subCluster.cluster, cluster).fetchJoin()
                .leftJoin(cluster.grupo, grupo).fetchJoin()
                .leftJoin(grupo.regional, regional).fetchJoin()
                .where(subCluster.id.eq(subClusterId))
                .orderBy(cidade.nome.asc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Cidade> findAllByRegionalId(Integer regionalId, Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(cidade)
                .from(cidade)
                .leftJoin(cidade.uf).fetchJoin()
                .leftJoin(cidade.subCluster, subCluster).fetchJoin()
                .leftJoin(subCluster.cluster, cluster).fetchJoin()
                .leftJoin(cluster.grupo, grupo).fetchJoin()
                .leftJoin(grupo.regional, regional).fetchJoin()
                .where(regional.id.eq(regionalId).and(predicate))
                .orderBy(grupo.nome.asc(),
                        cluster.nome.asc(),
                        subCluster.nome.asc(),
                        cidade.nome.asc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Cidade> findAllBySubClusterId(Integer subClusterId, Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(cidade)
                .from(cidade)
                .leftJoin(cidade.uf).fetchJoin()
                .leftJoin(cidade.subCluster, subCluster).fetchJoin()
                .leftJoin(subCluster.cluster, cluster).fetchJoin()
                .leftJoin(cluster.grupo, grupo).fetchJoin()
                .leftJoin(grupo.regional, regional).fetchJoin()
                .where(subCluster.id.eq(subClusterId).and(predicate))
                .orderBy(grupo.nome.asc(),
                        cluster.nome.asc(),
                        subCluster.nome.asc(),
                        cidade.nome.asc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Cidade> findAllBySubClustersId(List<Integer> subClustersId, Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(cidade)
                .from(cidade)
                .leftJoin(cidade.uf).fetchJoin()
                .leftJoin(cidade.subCluster, subCluster).fetchJoin()
                .leftJoin(subCluster.cluster, cluster).fetchJoin()
                .leftJoin(cluster.grupo, grupo).fetchJoin()
                .leftJoin(grupo.regional, regional).fetchJoin()
                .where(subCluster.id.in(subClustersId).and(predicate))
                .orderBy(grupo.nome.asc(),
                        cluster.nome.asc(),
                        subCluster.nome.asc(),
                        cidade.nome.asc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Cidade> findAllByGrupoId(Integer grupoId, Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(cidade)
                .from(cidade)
                .leftJoin(cidade.uf).fetchJoin()
                .leftJoin(cidade.subCluster, subCluster).fetchJoin()
                .leftJoin(subCluster.cluster, cluster).fetchJoin()
                .leftJoin(cluster.grupo, grupo).fetchJoin()
                .leftJoin(grupo.regional, regional).fetchJoin()
                .where(grupo.id.eq(grupoId).and(predicate))
                .orderBy(grupo.nome.asc(),
                        cluster.nome.asc(),
                        subCluster.nome.asc(),
                        cidade.nome.asc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Cidade> findAllByClusterId(Integer clusterId, Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(cidade)
                .from(cidade)
                .leftJoin(cidade.uf).fetchJoin()
                .leftJoin(cidade.subCluster, subCluster).fetchJoin()
                .leftJoin(subCluster.cluster, cluster).fetchJoin()
                .leftJoin(cluster.grupo, grupo).fetchJoin()
                .leftJoin(grupo.regional, regional).fetchJoin()
                .where(cluster.id.eq(clusterId).and(predicate))
                .orderBy(grupo.nome.asc(),
                        cluster.nome.asc(),
                        subCluster.nome.asc(),
                        cidade.nome.asc())
                .distinct()
                .fetch();
    }

    @Override
    public ClusterizacaoDto getClusterizacao(Integer id) {
        return new JPAQueryFactory(entityManager)
                .select(Projections.constructor(ClusterizacaoDto.class,
                        cidade.id,
                        cidade.nome,
                        subCluster.id,
                        subCluster.nome,
                        cluster.id,
                        cluster.nome,
                        grupo.id,
                        grupo.nome,
                        regional.id,
                        regional.nome))
                .from(cidade)
                .innerJoin(cidade.subCluster, subCluster)
                .innerJoin(subCluster.cluster, cluster)
                .innerJoin(cluster.grupo, grupo)
                .innerJoin(grupo.regional, regional)
                .where(cidade.id.eq(id))
                .fetchOne();
    }

    @Override
    public Optional<Cidade> findByPredicate(Predicate predicate) {
        return Optional.ofNullable(new JPAQueryFactory(entityManager)
                .select(cidade)
                .from(cidade)
                .where(predicate)
                .fetchOne());
    }

}
