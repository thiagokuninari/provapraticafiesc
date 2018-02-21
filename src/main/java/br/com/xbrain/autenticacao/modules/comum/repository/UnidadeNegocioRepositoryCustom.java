package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface UnidadeNegocioRepositoryCustom {

    List<UnidadeNegocio> findAll(Predicate predicate);

}
