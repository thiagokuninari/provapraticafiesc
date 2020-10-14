package br.com.xbrain.autenticacao.modules.site.repository;

import br.com.xbrain.autenticacao.modules.site.dto.SiteSupervisorResponse;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import com.querydsl.core.types.Predicate;

import java.util.List;
import java.util.Optional;

public interface SiteRepositoryCustom {

    Optional<Site> findById(Integer id);

    List<Site> findBySituacaoAtiva(Predicate predicate);

    List<Site> findByEstadoId(Integer estadoId);

    List<SiteSupervisorResponse> findSupervisoresBySiteIdAndUsuarioSuperiorId(Integer siteId, Integer usuarioSuperiorId);
}
