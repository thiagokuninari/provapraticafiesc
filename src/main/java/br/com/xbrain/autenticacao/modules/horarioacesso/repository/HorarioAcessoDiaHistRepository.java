package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcessoDiaHistorico;

public interface HorarioAcessoDiaHistRepository extends CrudRepository<HorarioAcessoDiaHistorico, Integer> {
    
}
