package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.DiaAcessoHistorico;

public interface DiaAcessoHistoricoRepository extends CrudRepository<DiaAcessoHistorico, Integer> {
    
}
