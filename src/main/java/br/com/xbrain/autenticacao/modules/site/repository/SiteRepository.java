package br.com.xbrain.autenticacao.modules.site.repository;

import br.com.xbrain.autenticacao.modules.site.model.Site;
import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer>,
    QueryDslPredicateExecutor<Site>, SiteRepositoryCustom {

    List<Site> findAll(Predicate predicate);

    @Modifying(clearAutomatically = true)
    @Query("update Site a set a.discadoraId = ?1 where a.id in (?2)")
    void updateDiscadoraBySites(Integer discadoraId, List<Integer> sites);

    @Modifying(clearAutomatically = true)
    @Query("update Site a set a.discadoraId = null where a.id = ?1")
    void removeDiscadoraBySite(Integer siteId);
}
