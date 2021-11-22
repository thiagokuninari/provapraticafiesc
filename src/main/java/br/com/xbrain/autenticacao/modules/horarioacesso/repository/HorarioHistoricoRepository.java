package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioHistorico;

public interface HorarioHistoricoRepository extends 
    CrudRepository<HorarioHistorico, Integer>,
    HorarioHistoricoRepositoryCustom {
    
}
