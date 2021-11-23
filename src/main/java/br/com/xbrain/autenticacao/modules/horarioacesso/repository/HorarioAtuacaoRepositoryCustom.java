package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;

import java.util.List;

public interface HorarioAtuacaoRepositoryCustom {
    
    List<HorarioAtuacao> findByHorarioAcessoId(Integer horarioAcessoId);

    List<HorarioAtuacao> findByHorarioHistoricoId(Integer horarioHistoricoId);
}
