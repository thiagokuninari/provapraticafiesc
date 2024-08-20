package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.SubNivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SubNivelRepository extends JpaRepository<SubNivel, Integer>,
    QueryDslPredicateExecutor<SubNivel>, SubNivelRepositoryCustom {

    List<SubNivel> findByNivelIdAndSituacao(Integer nivelId, ESituacao situacao);

    Set<SubNivel> findByIdIn(Set<Integer> ids);
}
