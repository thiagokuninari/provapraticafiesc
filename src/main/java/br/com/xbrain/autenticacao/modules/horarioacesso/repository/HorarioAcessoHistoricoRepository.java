package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcessoHistorico;

public interface HorarioAcessoHistoricoRepository extends 
        CrudRepository<HorarioAcessoHistorico, Integer>,
        HorarioAcessoHistoricoRepositoryCustom {

}
