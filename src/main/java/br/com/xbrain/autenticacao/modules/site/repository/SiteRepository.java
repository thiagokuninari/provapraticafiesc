package br.com.xbrain.autenticacao.modules.site.repository;

import br.com.xbrain.autenticacao.modules.site.model.Site;
import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer>,
    QueryDslPredicateExecutor<Site>, SiteRepositoryCustom {

    Optional<Site> findFirstByCidadesIdInAndIdNot(List<Integer> cidadesIds, Integer id);

    List<Site> findAll(Predicate predicate);
}
