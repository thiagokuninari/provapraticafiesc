package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.DiaAcesso;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface DiaAcessoRepository extends CrudRepository<DiaAcesso, Integer>, 
    QueryDslPredicateExecutor<DiaAcesso> {

    List<DiaAcesso> findByHorarioAcessoId(Integer horarioAcessoId);

    @Modifying
    @Query("delete from dia_acesso d where d.fk_horario_acesso = :horarioAcessoId")
    void delete(Integer horarioAcessoId);
}
