package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.service.RegionalService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CidadePredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.config.CacheConfig.CIDADES_DISTRITOS_CACHE_NAME;
import static br.com.xbrain.autenticacao.modules.comum.util.StreamUtils.distinctByKey;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class CidadeService {

    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Cidade não encontrada.");

    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private CidadeRepository cidadeRepository;
    @Autowired
    private AgenteAutorizadoService agenteAutorizadoService;
    @Autowired
    private RegionalService regionalService;
    @Lazy
    @Autowired
    private CidadeService self;

    private Supplier<BooleanBuilder> predicateCidadesPermitidas = () ->
        new CidadePredicate().filtrarPermitidos(autenticacaoService.getUsuarioAutenticado()).build();

    public Iterable<Cidade> buscarTodas(Integer idUf, Integer idRegional, Integer idSubCluster) {
        if (nonNull(idUf)) {
            if (nonNull(idRegional)) {
                return getAllCidadeByRegionalAndUf(idRegional, idUf);
            }

            return getAllCidadeByUf(idUf);
        } else if (nonNull(idSubCluster)) {
            return getAllBySubCluster(idSubCluster);
        }

        return Collections.emptyList();
    }

    public List<UsuarioCidadeDto> getAllByRegionalId(Integer regionalId) {
        return UsuarioCidadeDto.of(regionalService.getNovasRegionaisIds().contains(regionalId)
            ? cidadeRepository.findAllByNovaRegionalId(regionalId, predicateCidadesPermitidas.get())
            : cidadeRepository.findAllByRegionalId(regionalId, predicateCidadesPermitidas.get()));
    }

    public List<UsuarioCidadeDto> getAllByRegionalIdAndUfId(Integer regionalId, Integer ufId) {
        return UsuarioCidadeDto.of(
            cidadeRepository.findAllByRegionalIdAndUfId(regionalId, ufId, predicateCidadesPermitidas.get()));
    }

    public List<UsuarioCidadeDto> getCidadesByRegionalReprocessamento(Integer regionalId) {
        return UsuarioCidadeDto.of(
            cidadeRepository.findAllByNovaRegionalId(regionalId, new CidadePredicate().build()));
    }

    public List<UsuarioCidadeDto> getCidadesByRegionalAndUfReprocessamento(Integer regionalId, Integer ufId) {
        return UsuarioCidadeDto.of(
            cidadeRepository.findAllByRegionalIdAndUfId(regionalId, ufId, new CidadePredicate().build()));
    }

    public List<UsuarioCidadeDto> getAllBySubClusterId(Integer subClusterId) {
        return UsuarioCidadeDto.of(
            cidadeRepository.findAllBySubClusterId(
                subClusterId,
                predicateCidadesPermitidas.get()));
    }

    public List<UsuarioCidadeDto> getAllBySubClustersId(List<Integer> subClustersId) {
        return UsuarioCidadeDto.of(
            cidadeRepository.findAllBySubClustersId(
                subClustersId,
                predicateCidadesPermitidas.get()));
    }

    public List<UsuarioCidadeDto> getAllByGrupoId(Integer grupoId) {
        return UsuarioCidadeDto.of(
            cidadeRepository.findAllByGrupoId(
                grupoId,
                predicateCidadesPermitidas.get()));
    }

    public List<UsuarioCidadeDto> getAllByClusterId(Integer clusterId) {
        return UsuarioCidadeDto.of(
            cidadeRepository.findAllByClusterId(clusterId,
                predicateCidadesPermitidas.get()));
    }

    public List<Cidade> getAllCidadeByUf(Integer idUf) {
        return cidadeRepository.findCidadeByUfId(idUf, new Sort("nome"));
    }

    public List<Cidade> getAllCidadeByRegionalAndUf(Integer idRegional, Integer idUf) {
        return cidadeRepository.findAllByRegionalIdAndUfId(idRegional, idUf, new CidadePredicate().build());
    }

    public List<Cidade> getAllBySubCluster(Integer idSubCluster) {
        return cidadeRepository.findBySubCluster(idSubCluster);
    }

    public Cidade findByUfNomeAndCidadeNome(String uf, String cidade) {
        return cidadeRepository
            .findByPredicate(
                new CidadePredicate()
                    .comNome(cidade)
                    .comUf(uf)
                    .build())
            .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public Cidade findFirstByUfNomeAndCidadeNome(String uf, String cidade) {
        return cidadeRepository
            .findFirstByPredicate(
                new CidadePredicate()
                    .comNome(cidade)
                    .comUf(uf)
                    .build())
            .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public ClusterizacaoDto getClusterizacao(Integer id) {
        return cidadeRepository.getClusterizacao(id);
    }

    public List<SelectResponse> buscarCidadesPorEstadosIds(List<Integer> estadosIds) {
        if (!estadosIds.isEmpty()) {
            var cidades = cidadeRepository.findAllByUfIdInOrderByNome(estadosIds);

            if (cidades.isEmpty()) {
                return List.of();
            }

            var cidadesResponse = getListaCidadeResponseOrdenadaPorNome(cidades);
            var distritos = self.getCidadesDistritos(Eboolean.V);

            return cidadesResponse
                .stream()
                .map(cidadeResponse -> CidadeResponse.definirNomeCidadePaiPorDistritos(cidadeResponse, distritos))
                .map(cidadeResponse -> SelectResponse.of(cidadeResponse.getId(), cidadeResponse.getNomeComCidadePaiEUf()))
                .collect(Collectors.toList());
        }

        return List.of();
    }

    public CidadeSiteResponse getCidadeByCodigoCidadeDbm(Integer codigoCidadeDbm) {
        return cidadeRepository.findCidadeComSite(cidade.cidadesDbm.any().codigoCidadeDbm.eq(codigoCidadeDbm))
            .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public CidadeSiteResponse findCidadeComSiteByUfECidade(String uf, String cidadeNome) {
        return cidadeRepository.findCidadeComSite(cidade.uf.uf.eq(uf).and(cidade.nome.eq(cidadeNome)))
            .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public List<UsuarioCidadeDto> getAtivosParaComunicados(Integer subclusterId) {
        return Stream.concat(
                agenteAutorizadoService.getCidades(subclusterId).stream(),
                getAllBySubCluster(subclusterId).stream().map(UsuarioCidadeDto::of))
            .filter(distinctByKey(UsuarioCidadeDto::getIdCidade))
            .collect(Collectors.toList());
    }

    public List<CidadeUfResponse> getAllCidadeByUfs(List<Integer> ufIds) {
        if (!ufIds.isEmpty()) {
            var cidades = cidadeRepository.findAllByUfIdInOrderByNome(ufIds);

            return cidades
                .stream()
                .map(CidadeUfResponse::of)
                .map(cidadeUfResponse -> CidadeUfResponse.definirNomeCidadePai(cidadeUfResponse, cidades))
                .collect(Collectors.toList());
        }

        return List.of();
    }

    public CidadeResponse findCidadeByCodigoIbge(String codigoIbge) {
        return cidadeRepository.findCidadeByCodigoIbge(codigoIbge)
            .map(CidadeResponse::of)
            .orElse(null);
    }

    public List<CidadeResponse> findCidadesByCodigosIbge(List<String> codigosIbge) {
        return cidadeRepository.findCidadesByCodigosIbge(
                new CidadePredicate()
                    .comCodigosIbge(codigosIbge)
                    .build()
            )
            .stream()
            .map(CidadeResponse::of)
            .collect(Collectors.toList());
    }

    public Cidade findById(Integer id) {
        return cidadeRepository.findOne(id);
    }

    public List<CidadeResponse> getAllCidadeNetUno() {
        return cidadeRepository.findAllByNetUno(Eboolean.V)
            .stream()
            .map(CidadeResponse::of)
            .collect(Collectors.toList());
    }

    public List<CodigoIbgeRegionalResponse> getCodigoIbgeRegionalByCidadeNomeAndUf(CidadesUfsRequest cidadesUfs) {
        if (!cidadesUfs.getCidades().isEmpty() && !cidadesUfs.getUfs().isEmpty()) {
            var predicate = new CidadePredicate()
                .comCidadesUfs(cidadesUfs)
                .build();

            return cidadeRepository.findCodigoIbgeRegionalByCidadeNomeAndUf(predicate);
        }
        return List.of();
    }

    public List<CodigoIbgeRegionalResponse> getCodigoIbgeRegionalByCidade(List<Integer> cidadesId) {
        if (!cidadesId.isEmpty()) {
            var predicate = new CidadePredicate()
                .comCidadesId(cidadesId)
                .build();

            return cidadeRepository.findCodigoIbgeRegionalByCidade(predicate);
        }

        return List.of();
    }

    public List<CidadeResponse> getAll(Integer regionalId, Integer ufId) {
        var predicate = new CidadePredicate()
            .comRegionalId(regionalId)
            .comUfId(ufId)
            .build();

        var cidades = getCidadesByPredicate(predicate);

        if (cidades.isEmpty()) {
            return List.of();
        }

        var cidadesResponse = getListaCidadeResponseOrdenadaPorNome(cidades);
        var distritos = self.getCidadesDistritos(Eboolean.V);

        cidadesResponse
            .forEach(cidadeResponse -> CidadeResponse.definirNomeCidadePaiPorDistritos(cidadeResponse, distritos));

        return cidadesResponse;
    }

    public CidadeResponse getCidadeById(Integer cidadeId) {
        var cidade = Optional.ofNullable(cidadeRepository.findOne(cidadeId))
            .orElseThrow(() -> EX_NAO_ENCONTRADO);

        var cidadeResponse = CidadeResponse.of(cidade);

        if (cidadeResponse.getFkCidade() != null) {
            var cidadePai = cidadeRepository.findOne(cidade.getFkCidade());
            cidadeResponse.setCidadePai(cidadePai.getNome());
        }

        return cidadeResponse;
    }

    @Cacheable(CIDADES_DISTRITOS_CACHE_NAME)
    public Map<Integer, CidadeResponse> getCidadesDistritos(Eboolean apenasDistritos) {
        if (Eboolean.F == apenasDistritos) {
            return getCidadesByPredicate(new CidadePredicate().comDistritos(Eboolean.F).build())
                .stream()
                .map(CidadeResponse::of)
                .collect(Collectors.toMap(CidadeResponse::getId, cidadeResponse -> cidadeResponse));
        }

        var cidades = getCidadesByPredicate(new CidadePredicate().comDistritos(apenasDistritos).build());
        var cidadesResponse = getListaCidadeResponseOrdenadaPorNome(cidades);
        var cidadesPaiIds = getCidadesPaiIdsByCidadesResponse(cidadesResponse);
        var cidadesPai = getCidadesPaiByIds(cidadesPaiIds);

        return cidadesResponse
            .stream()
            .map(cidadeResponse -> CidadeResponse.definirNomeCidadePaiPorCidades(cidadeResponse, cidadesPai))
            .collect(Collectors.toMap(CidadeResponse::getId, cidadeResponse -> cidadeResponse));
    }

    @CacheEvict(
        cacheManager = "concurrentCacheManager",
        cacheNames = CIDADES_DISTRITOS_CACHE_NAME,
        allEntries = true)
    public void flushCacheCidadesDistritos() {
        log.info("Flush Cache Cidades Distritos");
    }

    public static List<CidadeResponse> getListaCidadeResponseOrdenadaPorNome(List<Cidade> cidades) {
        return cidades
            .stream()
            .map(CidadeResponse::of)
            .sorted(Comparator.comparing(CidadeResponse::getNome))
            .collect(Collectors.toList());
    }

    private List<Integer> getCidadesPaiIdsByCidadesResponse(List<CidadeResponse> cidadesResponse) {
        return cidadesResponse
            .stream()
            .filter(cidadeResponse -> hasFkCidadeSemNomeCidadePai(cidadeResponse.getFkCidade(), cidadeResponse.getCidadePai()))
            .map(CidadeResponse::getFkCidade)
            .distinct()
            .collect(Collectors.toList());
    }

    public static boolean hasFkCidadeSemNomeCidadePai(Integer fkCidade, String nomeCidadePai) {
        return fkCidade != null && nomeCidadePai == null;
    }

    private Map<Integer, Cidade> getCidadesPaiByIds(List<Integer> cidadesPaiIds) {
        return getCidadesByPredicate(new CidadePredicate().comCidadesId(cidadesPaiIds).build())
            .stream()
            .collect(Collectors.toMap(Cidade::getId, cidade -> cidade));
    }

    private List<Cidade> getCidadesByPredicate(Predicate predicate) {
        return cidadeRepository.findAllByPredicate(predicate);
    }

    public CidadeResponse getCidadeDistrito(String uf, String cidade, String distrito) {
        return cidadeRepository.buscarCidadeDistrito(uf, cidade, distrito)
            .map(CidadeResponse::of)
            .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }
}
