package br.com.xbrain.autenticacao.modules.site.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.site.model.QSite.site;

public class SiteRepositoryImpl extends CustomRepository<Site> implements SiteRepositoryCustom {

    @Override
    public Optional<Site> findById(Integer id) {
        return Optional.ofNullable(
            new JPAQueryFactory(entityManager)
                .selectFrom(site)
                .leftJoin(site.cidades).fetchJoin()
                .leftJoin(site.coordenadores).fetchJoin()
                .leftJoin(site.supervisores).fetchJoin()
                .leftJoin(site.estados).fetchJoin()
                .where(site.id.eq(id))
                .fetchOne());
    }

    @Override
    public List<Site> findBySituacaoAtiva() {
        return new JPAQueryFactory(entityManager)
            .selectFrom(site)
            .where(site.situacao.eq(ESituacao.A))
            .fetch();
    }
}
