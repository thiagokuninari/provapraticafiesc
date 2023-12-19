package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.service.RegionalService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.ParceirosOnlineService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CidadePredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.comum.util.StreamUtils.distinctByKey;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static java.util.Objects.nonNull;

@Service
public class CidadeService {

    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Cidade não encontrada.");

    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private CidadeRepository cidadeRepository;
    @Autowired
    private ParceirosOnlineService parceirosOnlineService;
    @Autowired
    private RegionalService regionalService;

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
        return cidadeRepository.findAllByUfIdInOrderByNome(estadosIds)
            .stream()
            .map(cidade -> SelectResponse.of(cidade.getId(), cidade.getNomeComUf()))
            .collect(Collectors.toList());
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
                parceirosOnlineService.getCidades(subclusterId).stream(),
                getAllBySubCluster(subclusterId).stream().map(UsuarioCidadeDto::of))
            .filter(distinctByKey(UsuarioCidadeDto::getIdCidade))
            .collect(Collectors.toList());
    }

    public List<CidadeUfResponse> getAllCidadeByUfs(List<Integer> ufIds) {
        return cidadeRepository.findCidadeByUfIdInOrderByNome(ufIds).stream()
            .map(CidadeUfResponse::of)
            .collect(Collectors.toList());
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
        return new ArrayList<>();
    }

    public List<CodigoIbgeRegionalResponse> getCodigoIbgeRegionalByCidade(List<Integer> cidadesId) {
        if (!cidadesId.isEmpty()) {
            var predicate = new CidadePredicate()
                .comCidadeId(cidadesId)
                .build();
            return cidadeRepository.findCodigoIbgeRegionalByCidade(predicate);
        }
        return new ArrayList<CodigoIbgeRegionalResponse>();
    }
}
