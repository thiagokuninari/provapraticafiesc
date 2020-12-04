package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface UfRepositoryCustom {

    List<Uf> buscarEstadosNaoAtribuidosEmSites(Predicate cidadePredicate);

    List<Uf> buscarEstadosNaoAtribuidosEmSitesExcetoPor(Predicate cidadePredicate, Integer siteId);

}
