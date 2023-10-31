package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeSiteResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.ClusterizacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.CodigoIbgeRegionalResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import com.querydsl.core.types.Predicate;

import java.util.List;
import java.util.Optional;

public interface CidadeRepositoryCustom {

    Iterable<Cidade> findBySubCluster(Integer subClusterId);

    Optional<Cidade> findByPredicate(Predicate predicate);

    List<Cidade> findAllByRegionalId(Integer regionalId, Predicate predicate);

    List<Cidade> findAllByNovaRegionalId(Integer regionalId, Predicate predicate);

    List<Cidade> findAllBySubClusterId(Integer subClusterId, Predicate predicate);

    List<Cidade> findAllBySubClustersId(List<Integer> subClustersId, Predicate predicate);

    List<Cidade> findAllByGrupoId(Integer grupoId, Predicate predicate);

    List<Cidade> findAllByClusterId(Integer clusterId, Predicate predicate);

    ClusterizacaoDto getClusterizacao(Integer id);

    List<Cidade> buscarCidadesVinculadasAoUsuarioSemSite(Predicate permissaoPredicate,
                                                         List<Integer> estadosIds);

    List<Cidade> buscarCidadesSemSitesPorEstadosIdsExcetoPor(Predicate permissaoPredicate,
                                                             List<Integer> estadosIds, Integer siteId);

    Optional<CidadeSiteResponse> findCidadeComSite(Predicate predicate);

    Optional<Cidade> findFirstByPredicate(Predicate predicate);

    List<CodigoIbgeRegionalResponse> findCodigoIbgeRegionalByCidadeNomeAndUf(Predicate predicate);

    List<Cidade> findAllByRegionalIdAndUfId(Integer regionalId, Integer ufId, Predicate predicate);

    List<CodigoIbgeRegionalResponse> findCodigoIbgeRegionalByCidade(Predicate predicate);

    List<Cidade> findCidadesByCodigosIbge(Predicate predicate);

    List<Cidade> findAllByPredicate(Predicate predicate);
}
