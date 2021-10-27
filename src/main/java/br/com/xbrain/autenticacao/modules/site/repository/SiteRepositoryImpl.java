package br.com.xbrain.autenticacao.modules.site.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.site.dto.SiteCidadeResponse;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.site.model.QSite.site;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;

public class SiteRepositoryImpl extends CustomRepository<Site> implements SiteRepositoryCustom {

    @Override
    public List<Site> findBySituacaoAtiva(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(site)
            .where(site.situacao.eq(ESituacao.A).and(predicate))
            .fetch();
    }

    @Override
    public List<Site> findByEstadoId(Integer estadoId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(site)
            .where(site.situacao.eq(ESituacao.A)
                .and(site.estados.contains(new Uf(estadoId))))
            .fetch();
    }

    @Override
    public Site findBySupervisorId(Integer supervisorId) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(site)
            .innerJoin(site.supervisores, usuario)
            .where(usuario.cargo.codigo.eq(CodigoCargo.SUPERVISOR_OPERACAO)
                .and(usuario.id.eq(supervisorId))
            .and(site.situacao.eq(ESituacao.A)))
            .fetchFirst();
    }

    @Override
    public Optional<SiteCidadeResponse> findSiteCidadeTop1ByPredicate(Predicate predicate) {
        return Optional.ofNullable(new JPAQueryFactory(entityManager)
            .select(Projections.constructor(SiteCidadeResponse.class,
                site.id,
                site.nome,
                cidade.codigoCidadeDbm,
                cidade.id,
                cidade.nome,
                cidade.uf.id,
                cidade.uf.uf
            ))
            .from(site)
            .innerJoin(site.cidades, cidade)
            .where(predicate)
            .fetchFirst());
    }

    @Override
    public List<Site> findAllByPredicate(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
            .select(site)
            .from(site)
            .where(predicate)
            .orderBy(site.id.asc())
            .fetch();
    }
}
