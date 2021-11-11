package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import java.util.List;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcessoHistorico;

public interface HorarioAcessoHistoricoRepositoryCustom {

    List<HorarioAcessoHistorico> findAllByHorarioAcesso(Integer horarioAcessoId);
}
