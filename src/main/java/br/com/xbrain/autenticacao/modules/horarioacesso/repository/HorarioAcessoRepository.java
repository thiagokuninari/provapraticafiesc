package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface HorarioAcessoRepository extends PagingAndSortingRepository<HorarioAcesso, Integer>,
    QueryDslPredicateExecutor<HorarioAcesso> {

    Optional<HorarioAcesso> findById(Integer id);
}
