package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcessoHistorico;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import javax.persistence.EntityManager;

import static br.com.xbrain.autenticacao.modules.horarioacesso.model.QHorarioAcesso.horarioAcesso;
import static br.com.xbrain.autenticacao.modules.horarioacesso.model.QHorarioAcessoHistorico.horarioAcessoHistorico;

public class HorarioAcessoHistoricoRepositoryImpl implements HorarioAcessoHistoricoRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<HorarioAcessoHistorico> findAllByHorarioAcesso(Integer horarioAcessoId) {
        return new JPAQueryFactory(entityManager)
            .select(horarioAcessoHistorico)
            .from(horarioAcessoHistorico)
            .leftJoin(horarioAcessoHistorico.horarioAcesso, horarioAcesso)
            .where(horarioAcesso.id.eq(horarioAcessoId))
            .fetch();
    }
  
}
