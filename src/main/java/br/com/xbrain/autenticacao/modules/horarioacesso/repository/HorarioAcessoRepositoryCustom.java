package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import java.util.Optional;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;

public interface HorarioAcessoRepositoryCustom {

    Optional<HorarioAcesso> findById(Integer horarioAcessoId);

    Optional<HorarioAcesso> findBySiteId(Integer siteId);
}
