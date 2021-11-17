package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import java.util.List;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.DiaAcessoHistorico;

public interface DiaAcessoHistoricoRepository extends 
    CrudRepository<DiaAcessoHistorico, Integer>,
    QueryDslPredicateExecutor<DiaAcessoHistorico> {
    
    List<DiaAcessoHistorico> findByHorarioAcessoHistoricoId(Integer horarioAcessoHistoricoId);
}
