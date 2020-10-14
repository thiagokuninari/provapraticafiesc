package br.com.xbrain.autenticacao.modules.site.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.site.dto.SiteSupervisorResponse;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.site.model.QSite.site;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioHierarquia.usuarioHierarquia;

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
    public List<SiteSupervisorResponse> findSupervisoresBySiteIdAndUsuarioSuperiorId(Integer siteId, Integer usuarioSuperiorId) {
        return new JPAQueryFactory(entityManager)
            .select(Projections.fields(SiteSupervisorResponse.class, usuario.id, usuario.nome))
            .from(site)
            .innerJoin(site.supervisores, usuario)
            .innerJoin(usuario.usuariosHierarquia, usuarioHierarquia)
            .on(usuarioHierarquia.usuarioSuperior.id.eq(usuarioSuperiorId))
            .where(site.id.eq(siteId))
            .distinct()
            .fetch();
    }
}
