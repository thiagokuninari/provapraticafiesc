package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.DiaAcesso;

import java.util.List;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface DiaAcessoRepository extends CrudRepository<DiaAcesso, Integer>, 
    QueryDslPredicateExecutor<DiaAcesso> {

    List<DiaAcesso> findByHorarioAcessoId(Integer horarioAcessoId);

    void deleteByHorarioAcessoId(Integer horarioAcessoId);
}
