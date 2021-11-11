package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import java.util.Optional;

import javax.persistence.EntityManager;

import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;

import static br.com.xbrain.autenticacao.modules.horarioacesso.model.QHorarioAcesso.horarioAcesso;
import static br.com.xbrain.autenticacao.modules.site.model.QSite.site;

public class HorarioAcessoRepositoryImpl implements HorarioAcessoRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Optional<HorarioAcesso> findById(Integer horarioAcessoId) {
        return Optional.ofNullable(
            new JPAQueryFactory(entityManager)
                .select(horarioAcesso)
                .from(horarioAcesso)
                .where(horarioAcesso.id.eq(horarioAcessoId))
                .fetchOne());
    }

    @Override
    public Optional<HorarioAcesso> findBySiteId(Integer siteId) {
        return Optional.ofNullable(
            new JPAQueryFactory(entityManager)
                .select(horarioAcesso)
                .from(horarioAcesso)
                .leftJoin(horarioAcesso.site, site)
                .where(site.id.eq(siteId))
                .fetchOne());
    }

}
