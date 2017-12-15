package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.model.QCluster;
import br.com.xbrain.autenticacao.modules.comum.model.QRegional;
import br.com.xbrain.autenticacao.modules.comum.model.QUf;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeAutoCompleteDto;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidade;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.model.QGrupo.grupo;
import static br.com.xbrain.autenticacao.modules.comum.model.QSubCluster.subCluster;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;

public class CidadeRepositoryImpl extends CustomRepository<Cidade> implements CidadeRepositoryCustom {

    @Override
    public List<CidadeAutoCompleteDto> findAllAtivas() {
        return new JPAQueryFactory(entityManager)
                .select(cidade.id, cidade.nome, cidade.uf.uf)
                .from(cidade)
                .orderBy(cidade.nome.asc())
                .fetch()
                .stream()
                .map(t -> new CidadeAutoCompleteDto(
                        t.get(cidade.id),
                        t.get(cidade.nome),
                        t.get(cidade.uf.uf)))
                .collect(Collectors.toList());
    }

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
    public Iterable<Cidade> findByRegional(Integer regionalId) {
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
    public Iterable<Cidade> findByGrupo(Integer grupoId) {
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
    public Iterable<Cidade> findByCluster(Integer clusterId) {
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
    public List<String> findByUf(String uf) {
        return new JPAQueryFactory(entityManager)
                .select(cidade.nome)
                .from(cidade)
                .where(cidade.uf.uf.endsWithIgnoreCase(uf))
                .orderBy(cidade.nome.asc())
                .fetch();
    }

    @Override
    public List<Cidade> find(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(cidade)
                .from(cidade)
                .innerJoin(cidade.uf, QUf.uf1).fetchJoin()
                .innerJoin(cidade.subCluster, subCluster)
                .innerJoin(subCluster.cluster, QCluster.cluster)
                .innerJoin(QCluster.cluster.grupo, grupo)
                .innerJoin(grupo.regional, QRegional.regional)
                .where(predicate)
                .orderBy(
                        cidade.uf.uf.asc(),
                        cidade.nome.asc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Cidade> findByUsuarioId(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
                .select(cidade)
                .from(cidade)
                .join(cidade.cidadeUsuarios, QUsuarioCidade.usuarioCidade)
                .where(QUsuarioCidade.usuarioCidade.usuario.id.eq(usuarioId))
                .orderBy(cidade.nome.asc())
                .distinct()
                .fetch();
    }
}
