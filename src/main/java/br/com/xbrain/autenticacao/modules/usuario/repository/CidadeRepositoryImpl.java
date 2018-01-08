package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.model.QCluster;
import br.com.xbrain.autenticacao.modules.comum.model.QRegional;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.QCidade;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.comum.model.QGrupo.grupo;
import static br.com.xbrain.autenticacao.modules.comum.model.QSubCluster.subCluster;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;

public class CidadeRepositoryImpl extends CustomRepository<Cidade> implements CidadeRepositoryCustom {

    @Override
    public Iterable<Cidade> findBySubCluster(Integer subClusterId) {
        return new JPAQueryFactory(entityManager)
                .select(cidade)
                .from(cidade)
                .leftJoin(cidade.uf).fetchJoin()
                .leftJoin(cidade.subCluster, subCluster).fetchJoin()
                .leftJoin(subCluster.cluster, QCluster.cluster).fetchJoin()
                .leftJoin(QCluster.cluster.grupo, grupo).fetchJoin()
                .leftJoin(grupo.regional, QRegional.regional).fetchJoin()
                .where(subCluster.id.eq(subClusterId))
                .orderBy(cidade.nome.asc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Cidade> findAllByRegionalId(Integer regionalId) {
        return new JPAQueryFactory(entityManager)
                .select(cidade)
                .from(cidade)
                .leftJoin(cidade.uf).fetchJoin()
                .leftJoin(cidade.subCluster, subCluster).fetchJoin()
                .leftJoin(subCluster.cluster, QCluster.cluster).fetchJoin()
                .leftJoin(QCluster.cluster.grupo, grupo).fetchJoin()
                .leftJoin(grupo.regional, QRegional.regional).fetchJoin()
                .where(QRegional.regional.id.eq(regionalId))
                .orderBy(cidade.nome.asc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Cidade> findAllBySubClusterId(Integer subClusterId) {
        return new JPAQueryFactory(entityManager)
                .select(cidade)
                .from(cidade)
                .leftJoin(cidade.uf).fetchJoin()
                .leftJoin(cidade.subCluster, subCluster).fetchJoin()
                .leftJoin(subCluster.cluster, QCluster.cluster).fetchJoin()
                .leftJoin(QCluster.cluster.grupo, grupo).fetchJoin()
                .leftJoin(grupo.regional, QRegional.regional).fetchJoin()
                .where(subCluster.id.eq(subClusterId))
                .orderBy(cidade.nome.asc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Cidade> findAllByGrupoId(Integer grupoId) {
        return new JPAQueryFactory(entityManager)
                .select(cidade)
                .from(cidade)
                .leftJoin(cidade.uf).fetchJoin()
                .leftJoin(cidade.subCluster, subCluster).fetchJoin()
                .leftJoin(subCluster.cluster, QCluster.cluster).fetchJoin()
                .leftJoin(QCluster.cluster.grupo, grupo).fetchJoin()
                .leftJoin(grupo.regional, QRegional.regional).fetchJoin()
                .where(grupo.id.eq(grupoId))
                .orderBy(cidade.nome.asc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Cidade> findAllByClusterId(Integer clusterId) {
        return new JPAQueryFactory(entityManager)
                .select(cidade)
                .from(cidade)
                .leftJoin(cidade.uf).fetchJoin()
                .leftJoin(cidade.subCluster, subCluster).fetchJoin()
                .leftJoin(subCluster.cluster, QCluster.cluster).fetchJoin()
                .leftJoin(QCluster.cluster.grupo, grupo).fetchJoin()
                .leftJoin(grupo.regional, QRegional.regional).fetchJoin()
                .where(QCluster.cluster.id.eq(clusterId))
                .orderBy(cidade.nome.asc())
                .distinct()
                .fetch();
    }

    @Override
    public Optional<Cidade> findByPredicate(Predicate predicate) {
        return Optional.ofNullable(new JPAQueryFactory(entityManager)
                .select(QCidade.cidade)
                .from(QCidade.cidade)
                .where(predicate)
                .fetchOne());
    }

}
