package br.com.xbrain.autenticacao.modules.site.repository;

import br.com.xbrain.autenticacao.modules.site.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends PagingAndSortingRepository<Site, Integer>, JpaRepository<Site, Integer>,
        QueryDslPredicateExecutor<Site>, SiteRepositoryCustom {

}
