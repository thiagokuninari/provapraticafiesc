package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface SubCanalRepository extends PagingAndSortingRepository<SubCanal, Integer>,
    QueryDslPredicateExecutor<SubCanal>, SubCanalRepositoryCustom {

    SubCanal findByCodigo(String codigo);

    Optional<SubCanal> findById(Integer id);
}
