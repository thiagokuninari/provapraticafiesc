package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.SubNivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubNivelRepository extends JpaRepository<SubNivel, Integer>,
    QueryDslPredicateExecutor<SubNivel> {

    List<SubNivel> findByIdIn(List<Integer> ids);
}
