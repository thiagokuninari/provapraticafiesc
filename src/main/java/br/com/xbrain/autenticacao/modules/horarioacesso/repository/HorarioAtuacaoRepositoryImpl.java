package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import java.util.List;
import javax.persistence.EntityManager;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static br.com.xbrain.autenticacao.modules.horarioacesso.model.QHorarioAtuacao.horarioAtuacao;

public class HorarioAtuacaoRepositoryImpl implements HorarioAtuacaoRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<HorarioAtuacao> findByHorarioAcessoId(Integer horarioAcessoId) {
        return new JPAQueryFactory(entityManager)
            .select(horarioAtuacao)
            .from(horarioAtuacao)
            .where(horarioAtuacao.horarioAcesso.id.eq(horarioAcessoId))
            .fetch();
    }

    @Override
    public List<HorarioAtuacao> findByHorarioHistoricoId(Integer horarioHistoricoId) {
        return new JPAQueryFactory(entityManager)
            .select(horarioAtuacao)
            .from(horarioAtuacao)
            .where(horarioAtuacao.horarioHistorico.id.eq(horarioHistoricoId))
            .fetch();
    }
    
}
