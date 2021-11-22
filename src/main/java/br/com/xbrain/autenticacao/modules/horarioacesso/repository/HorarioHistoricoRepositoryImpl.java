package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import java.util.List;

import javax.persistence.EntityManager;

import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioHistorico;

import static br.com.xbrain.autenticacao.modules.horarioacesso.model.QHorarioHistorico.horarioHistorico;

public class HorarioHistoricoRepositoryImpl implements HorarioHistoricoRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<HorarioHistorico> findByHorarioAcessoId(Integer horarioAcessoId) {
        return new JPAQueryFactory(entityManager)
            .select(horarioHistorico)
            .from(horarioHistorico)
            .where(horarioHistorico.horarioAcesso.id.eq(horarioAcessoId))
            .fetch();
    }
    
}
