package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.model.QCluster.cluster;
import static br.com.xbrain.autenticacao.modules.comum.model.QGrupo.grupo;
import static br.com.xbrain.autenticacao.modules.comum.model.QRegional.regional;
import static br.com.xbrain.autenticacao.modules.comum.model.QSubCluster.subCluster;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;

@SuppressWarnings("PMD.TooManyStaticImports")
public class RegionalRepositoryImpl extends CustomRepository<Regional> implements RegionalRepositoryCustom {

    @Override
    public List<Regional> getAll(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(regional)
                .from(regional)
                .where(regional.situacao.eq(ESituacao.A).and(predicate))
                .orderBy(regional.nome.asc())
                .fetch();
    }

    @Override
    public List<Regional> getAllByUsuarioId(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
                .select(regional)
                .from(usuarioCidade)
                .innerJoin(usuarioCidade.cidade, cidade)
                .innerJoin(cidade.subCluster, subCluster)
                .innerJoin(subCluster.cluster, cluster)
                .innerJoin(cluster.grupo, grupo)
                .innerJoin(grupo.regional, regional)
                .where(usuarioCidade.usuario.id.eq(usuarioId)
                        .and(usuarioCidade.dataBaixa.isNull()))
                .orderBy(regional.nome.asc())
                .distinct()
                .fetch();
    }

    @Override
    public List<Integer> getNovasRegionaisIds() {
        return new JPAQueryFactory(entityManager)
                .select(regional.id)
                .from(regional)
                .where(regional.novaRegional.eq(Eboolean.V))
                .fetch();
    }
}
