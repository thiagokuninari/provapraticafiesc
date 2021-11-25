package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import static br.com.xbrain.autenticacao.modules.horarioacesso.model.QHorarioHistorico.horarioHistorico;
import static br.com.xbrain.autenticacao.modules.horarioacesso.model.QHorarioAtuacao.horarioAtuacao;

public class HorarioHistoricoRepositoryImpl implements HorarioHistoricoRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public PageImpl<HorarioAcessoResponse> findByHorarioAcessoId(Pageable pageable, Integer horarioAcessoId) {
        var historicos = new JPAQueryFactory(entityManager)
            .select(horarioHistorico)
            .from(horarioHistorico)
            .where(horarioHistorico.horarioAcesso.id.eq(horarioAcessoId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(horarioHistorico.id.asc())
            .fetch()
            .stream()
            .map(HorarioAcessoResponse::of)
            .collect(Collectors.toList());
        historicos.forEach(historico -> historico.setHorariosAtuacao(
            getHorariosAtuacao(historico.getHorarioHistoricoId())));
        return new PageImpl<>(historicos, pageable, countHorariosAcesso(horarioAcessoId));
    }

    private long countHorariosAcesso(Integer horarioAcessoId) {
        return new JPAQueryFactory(entityManager)
            .select(horarioHistorico)
            .from(horarioHistorico)
            .where(horarioHistorico.horarioAcesso.id.eq(horarioAcessoId))
            .fetchCount();
    }

    private List<HorarioAtuacao> getHorariosAtuacao(Integer horarioHistoricoId) {
        return new JPAQueryFactory(entityManager)
            .select(horarioAtuacao)
            .from(horarioAtuacao)
            .where(horarioAtuacao.horarioHistorico.id.eq(horarioHistoricoId))
            .fetch();
    }
}
