package br.com.xbrain.autenticacao.modules.logrequest.repository;

import br.com.xbrain.autenticacao.modules.logrequest.model.LogRequest;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface LogRequestRepository extends PagingAndSortingRepository<LogRequest, Integer>,
        QueryDslPredicateExecutor<LogRequest> {

    List<LogRequest> findAll();
}
