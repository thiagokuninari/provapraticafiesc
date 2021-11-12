package br.com.xbrain.autenticacao.modules.horarioacesso.repository;

import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.Predicate;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcesso;

public interface HorarioAcessoRepositoryCustom {

    Optional<HorarioAcesso> findById(Integer horarioAcessoId);

    Optional<HorarioAcesso> findBySiteId(Integer siteId);

    List<HorarioAcesso> findAll(Predicate predicate);
}
