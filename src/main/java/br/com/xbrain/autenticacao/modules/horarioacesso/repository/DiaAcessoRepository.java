package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.DiaAcesso;

public interface DiaAcessoRepository extends CrudRepository<DiaAcesso, Integer> {
    
}
