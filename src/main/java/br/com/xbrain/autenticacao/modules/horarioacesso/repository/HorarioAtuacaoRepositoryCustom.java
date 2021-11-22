package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import java.util.List;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;

public interface HorarioAtuacaoRepositoryCustom {
    
    List<HorarioAtuacao> findByHorarioAcessoId(Integer horarioAcessoId);

    List<HorarioAtuacao> findByHorarioHistoricoId(Integer horarioHistoricoId);
}
