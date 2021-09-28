package br.com.xbrain.autenticacao.modules.site.repository;

import br.com.xbrain.autenticacao.modules.site.dto.SiteCidadeResponse;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import com.querydsl.core.types.Predicate;

import java.util.List;
import java.util.Optional;

public interface SiteRepositoryCustom {

    List<Site> findBySituacaoAtiva(Predicate predicate);

    List<Site> findByEstadoId(Integer estadoId);

    Site findBySupervisorId(Integer supervisorId);

    Optional<SiteCidadeResponse> findSiteCidadeTop1ByPredicate(Predicate predicate);

    List<Site> findAllByPredicate(Predicate predicate);
}
