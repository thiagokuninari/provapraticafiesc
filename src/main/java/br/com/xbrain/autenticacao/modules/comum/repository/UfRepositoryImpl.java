package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.model.QUf.uf1;
import static br.com.xbrain.autenticacao.modules.site.model.QSite.site;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static com.querydsl.jpa.JPAExpressions.select;

public class UfRepositoryImpl extends CustomRepository<Uf> implements UfRepositoryCustom {

    @Override
    public List<Uf> buscarEstadosNaoAtribuidosEmSites() {
        return new JPAQueryFactory(entityManager)
            .select(Projections.fields(Uf.class, uf1.id, uf1.nome, uf1.uf))
            .from(cidade)
            .join(cidade.uf, uf1)
            .where(cidade.id.notIn(
                select(cidade.id)
                    .from(site)
                .join(site.cidades, cidade)
            ))
            .groupBy(uf1.id, uf1.nome, uf1.uf)
            .orderBy(uf1.nome.asc())
            .fetch();
    }

    @Override
    public List<Uf> buscarEstadosNaoAtribuidosEmSitesExcetoPor(Integer siteId) {
        return new JPAQueryFactory(entityManager)
            .select(Projections.fields(Uf.class, uf1.id, uf1.nome, uf1.uf))
            .from(cidade)
            .join(cidade.uf, uf1)
            .where(cidade.id.notIn(
                select(cidade.id)
                    .from(site)
                    .join(site.cidades, cidade)
                .where(site.id.ne(siteId))
            ))
            .groupBy(uf1.id, uf1.nome, uf1.uf)
            .orderBy(uf1.nome.asc())
            .fetch();
    }
}
