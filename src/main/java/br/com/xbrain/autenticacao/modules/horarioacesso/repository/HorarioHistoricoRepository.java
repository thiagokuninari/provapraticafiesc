package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioHistorico;

public interface HorarioHistoricoRepository extends PagingAndSortingRepository<HorarioHistorico, Integer>, 
    QueryDslPredicateExecutor<HorarioHistorico> {

    Page<HorarioHistorico> findByHorarioAcessoId(Integer horarioAcessoId, Pageable pageable);
}
