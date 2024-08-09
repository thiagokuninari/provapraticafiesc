package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Subnivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SubnivelRepository extends JpaRepository<Subnivel, Integer>,
    QueryDslPredicateExecutor<Subnivel> {

    List<Subnivel> findByNivelIdAndSituacao(Integer nivelId, ESituacao situacao);

    Set<Subnivel> findByIdIn(Set<Integer> ids);
}
