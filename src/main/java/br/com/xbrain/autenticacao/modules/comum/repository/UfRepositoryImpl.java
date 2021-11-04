package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.QRegional;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.usuario.model.QUsuario;
import br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.model.QUf.uf1;
import static br.com.xbrain.autenticacao.modules.site.model.QSite.site;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static com.querydsl.jpa.JPAExpressions.select;

public class UfRepositoryImpl extends CustomRepository<Uf> implements UfRepositoryCustom {

    @Override
    public List<Uf> buscarEstadosNaoAtribuidosEmSites(Predicate cidadePredicate) {
        return new JPAQueryFactory(entityManager)
            .select(Projections.fields(Uf.class, uf1.id, uf1.nome, uf1.uf))
            .from(QUsuario.usuario)
            .join(QUsuario.usuario.cidades, QUsuarioCidade.usuarioCidade)
            .join(QUsuarioCidade.usuarioCidade.cidade.uf, uf1)
            .where(
                QUsuarioCidade.usuarioCidade.cidade.id.notIn(
                select(cidade.id)
                    .from(site)
                    .join(site.cidades, cidade)
                    .where(site.situacao.eq(ESituacao.A)))
                    .and(cidadePredicate))
            .groupBy(uf1.id, uf1.nome, uf1.uf)
            .orderBy(uf1.nome.asc())
            .fetch();
    }

    @Override
    public List<Uf> buscarEstadosNaoAtribuidosEmSitesExcetoPor(Predicate cidadePredicate, Integer siteId) {
        return new JPAQueryFactory(entityManager)
            .select(Projections.fields(Uf.class, uf1.id, uf1.nome, uf1.uf))
            .from(QUsuario.usuario)
            .join(QUsuario.usuario.cidades, QUsuarioCidade.usuarioCidade)
            .join(QUsuarioCidade.usuarioCidade.cidade.uf, uf1)
            .where(QUsuarioCidade.usuarioCidade.cidade.id.notIn(
                select(cidade.id)
                    .from(site)
                    .join(site.cidades, cidade)
                .where(site.situacao.ne(ESituacao.I)
                    .and(site.id.ne(siteId)))
            ).and(cidadePredicate))
            .groupBy(uf1.id, uf1.nome, uf1.uf)
            .orderBy(uf1.nome.asc())
            .fetch();
    }

    @Override
    public List<Uf> buscarEstadosPorRegional(Integer regionalId) {
        return new JPAQueryFactory(entityManager)
            .select(uf1)
            .from(uf1)
            .join(uf1.regionais, QRegional.regional)
            .where(QRegional.regional.id.eq(regionalId))
            .groupBy(uf1.id, uf1.nome, uf1.uf)
            .orderBy(uf1.nome.asc())
            .fetch();
    }
}
