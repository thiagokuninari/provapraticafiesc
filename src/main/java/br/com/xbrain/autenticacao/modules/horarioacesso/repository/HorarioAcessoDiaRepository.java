package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcessoDia;

public interface HorarioAcessoDiaRepository extends CrudRepository<HorarioAcessoDia, Integer> {
    
}
