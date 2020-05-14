package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.model.Uf;

import java.util.List;

public interface UfRepositoryCustom {

    List<Uf> buscarEstadosNaoAtribuidosEmSites();

    List<Uf> buscarEstadosNaoAtribuidosEmSitesExcetoPor(Integer siteId);

}
