package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioHistorico;

import java.util.List;

public interface HorarioHistoricoRepositoryCustom {
    
    List<HorarioHistorico> findByHorarioAcessoId(Integer horarioAcessoId);
}
