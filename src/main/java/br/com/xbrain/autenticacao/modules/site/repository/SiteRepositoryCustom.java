package br.com.xbrain.autenticacao.modules.site.repository;

import br.com.xbrain.autenticacao.modules.site.model.Site;

import java.util.Optional;

public interface SiteRepositoryCustom {

    Optional<Site> findById(Integer id);
}
