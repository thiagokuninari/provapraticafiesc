package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.Predicate;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;

public interface HorarioAcessoRepository extends 
    CrudRepository<HorarioAcesso, Integer>,
    QueryDslPredicateExecutor<HorarioAcesso> {
    
    List<HorarioAcesso> findAll(Predicate predicate);

    Optional<HorarioAcesso> findById(Integer id);
}
