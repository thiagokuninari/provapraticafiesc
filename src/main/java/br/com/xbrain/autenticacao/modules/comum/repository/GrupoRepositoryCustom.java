package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.model.Grupo;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface GrupoRepositoryCustom {

    List<Grupo> findAllByRegionalId(Integer regionalId, Predicate predicate);
}
