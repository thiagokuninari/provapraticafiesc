package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;

public interface HorarioAtuacaoRepository extends 
    CrudRepository<HorarioAtuacao, Integer>, HorarioAtuacaoRepositoryCustom {
    
}
