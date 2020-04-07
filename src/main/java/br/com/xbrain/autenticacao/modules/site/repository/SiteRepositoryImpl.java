package br.com.xbrain.autenticacao.modules.site.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.infra.JoinDescriptor.leftJoin;
import static br.com.xbrain.autenticacao.modules.site.model.QSite.site;

public class SiteRepositoryImpl extends CustomRepository<Site> implements SiteRepositoryCustom {

    @Override
    public Page<Site> findAll(Predicate predicate, Pageable pageable) {
        return super.findAll(
                List.of(leftJoin(site.cidades),
                        leftJoin(site.coordenadores),
                        leftJoin(site.supervisores)),
                predicate,
                pageable);
    }

    @Override
    public Optional<Site> findById(Integer id) {
        return Optional.ofNullable(
                new JPAQueryFactory(entityManager)
                .selectFrom(site)
                .leftJoin(site.cidades).fetchJoin()
                .leftJoin(site.coordenadores).fetchJoin()
                .leftJoin(site.supervisores).fetchJoin()
                .where(site.id.eq(id))
                .fetchOne());
    }
}
