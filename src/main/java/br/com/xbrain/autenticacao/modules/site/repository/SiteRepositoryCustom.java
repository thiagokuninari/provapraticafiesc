package br.com.xbrain.autenticacao.modules.site.repository;

import br.com.xbrain.autenticacao.modules.site.model.Site;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SiteRepositoryCustom {

    Page<Site> findAll(Predicate predicate, Pageable pageable);

    Optional<Site> findById(Integer id);
}
