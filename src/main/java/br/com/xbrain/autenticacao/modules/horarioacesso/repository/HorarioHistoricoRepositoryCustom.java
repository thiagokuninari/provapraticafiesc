package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import java.util.List;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioHistorico;

public interface HorarioHistoricoRepositoryCustom {
    
    List<HorarioHistorico> findByHorarioAcessoId(Integer horarioAcessoId);
}
