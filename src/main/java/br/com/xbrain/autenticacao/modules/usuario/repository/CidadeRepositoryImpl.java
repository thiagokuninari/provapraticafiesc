package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeSiteResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.ClusterizacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.CodigoIbgeRegionalResponse;
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
import static br.com.xbrain.autenticacao.modules.comum.model.QUf.uf1;
import static br.com.xbrain.autenticacao.modules.site.model.QSite.site;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;
import static com.querydsl.jpa.JPAExpressions.select;

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
    public List<Cidade> buscarCidadesVinculadasAoUsuarioSemSite(Predicate permissaoPredicate,
                                                                List<Integer> estadosIds) {

        return new JPAQueryFactory(entityManager)
            .select(usuarioCidade.cidade)
            .from(usuario, usuario)
            .join(usuario.cidades, usuarioCidade)
            .join(usuarioCidade.cidade.uf, uf1).distinct()
            .where(uf1.id.in(estadosIds)
                .and(usuarioCidade.cidade.id.notIn(
                    select(cidade.id)
                        .from(site)
                        .join(site.cidades, cidade)
                        .where(site.situacao.eq(ESituacao.A))))
                .and(permissaoPredicate))
            .orderBy(usuarioCidade.cidade.nome.asc())
            .fetch();
    }

    @Override
    public List<Cidade> buscarCidadesSemSitesPorEstadosIdsExcetoPor(Predicate permissaoPredicate,
                                                                    List<Integer> estadosIds, Integer siteId) {
        return new JPAQueryFactory(entityManager)
            .select(usuarioCidade.cidade)
            .from(usuario, usuario)
            .join(usuario.cidades, usuarioCidade)
            .join(usuarioCidade.cidade.uf, uf1).distinct()
            .where(uf1.id.in(estadosIds)
                .and(usuarioCidade.cidade.id.notIn(
                    select(cidade.id)
                        .from(site)
                        .join(site.cidades, cidade)
                        .where(site.situacao.ne(ESituacao.I)
                            .and(site.id.ne(siteId)))
                ))
                .and(permissaoPredicate))
            .orderBy(usuarioCidade.cidade.nome.asc())
            .fetch();
    }

    @Override
    public Optional<CidadeSiteResponse> findCidadeComSite(Predicate predicate) {
        return Optional.ofNullable(new JPAQueryFactory(entityManager)
            .select(Projections.constructor(CidadeSiteResponse.class,
                cidade.id,
                site.id,
                cidade.nome,
                cidade.uf.uf
            ))
            .from(site)
            .where(predicate)
            .innerJoin(site.cidades, cidade)
            .fetchOne());
    }

    @Override
    public Optional<Cidade> findByPredicate(Predicate predicate) {
        return Optional.ofNullable(new JPAQueryFactory(entityManager)
            .select(cidade)
            .from(cidade)
            .where(predicate)
            .fetchOne());
    }

    @Override
    public Optional<Cidade> findFirstByPredicate(Predicate predicate) {
        return Optional.ofNullable(new JPAQueryFactory(entityManager)
            .select(cidade)
            .from(cidade)
            .where(predicate)
            .fetchFirst());
    }

    public List<CodigoIbgeRegionalResponse> findCodigoIbgeRegionalByCidadeNomeAndUf(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
            .select(Projections.constructor(CodigoIbgeRegionalResponse.class,
                cidade.id,
                cidade.nome,
                cidade.codigoIbge,
                regional.id,
                regional.nome,
                cidade.uf.id,
                cidade.uf.nome,
                cidade.uf.uf
            ))
            .from(cidade)
            .where(predicate)
            .innerJoin(cidade.subCluster, subCluster)
            .innerJoin(subCluster.cluster, cluster)
            .innerJoin(cluster.grupo, grupo)
            .innerJoin(grupo.regional, regional)
            .fetch();
    }
}
