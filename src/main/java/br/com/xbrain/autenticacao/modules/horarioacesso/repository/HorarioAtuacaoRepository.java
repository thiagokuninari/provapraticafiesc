package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import java.util.List;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;

public interface HorarioAtuacaoRepository extends CrudRepository<HorarioAtuacao, Integer>,
    QueryDslPredicateExecutor<HorarioAtuacao> {

    List<HorarioAtuacao> findByHorarioAcesso(Integer horarioAcessoId);
    
    List<HorarioAtuacao> findByHorarioHistorico(Integer horarioHistoricoId);
}
