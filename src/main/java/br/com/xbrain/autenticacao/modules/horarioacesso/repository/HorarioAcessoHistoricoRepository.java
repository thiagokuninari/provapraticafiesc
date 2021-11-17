package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcessoHistorico;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface HorarioAcessoHistoricoRepository extends 
    CrudRepository<HorarioAcessoHistorico, Integer> {

    List<HorarioAcessoHistorico> findByHorarioAcessoId(Integer horarioAcessoId);
}
