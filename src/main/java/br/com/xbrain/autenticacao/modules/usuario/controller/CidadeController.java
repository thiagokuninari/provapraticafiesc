package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/cidades")
public class CidadeController {

    @Autowired
    private CidadeService service;

    @GetMapping
    public Iterable<Cidade> buscarTodas(Integer idUf, Integer idRegional, Integer idSubCluster) {
        return service.buscarTodas(idUf, idRegional, idSubCluster);
    }

    @GetMapping("uf-cidade/{uf}/{cidade}")
    public CidadeResponse getByUfAndNome(@PathVariable String uf, @PathVariable String cidade) {
        return CidadeResponse.of(service.findByUfNomeAndCidadeNome(uf, cidade));
    }

    @GetMapping("{uf}/{cidade}/site")
    public CidadeSiteResponse getCidadeSiteByUfAndNome(@PathVariable String uf, @PathVariable String cidade) {
        return service.findCidadeComSiteByUfECidade(uf, cidade);
    }

    @GetMapping("recuperar-cidade/{uf}/{cidade}")
    public CidadeSubClusterResponse getCidadeSubcluster(@PathVariable String uf, @PathVariable String cidade) {
        return CidadeSubClusterResponse.parse(service.findByUfNomeAndCidadeNome(uf, cidade));
    }

    @GetMapping("regional/{regionalId}")
    public List<UsuarioCidadeDto> getByIdRegional(@PathVariable Integer regionalId) {
        return service.getAllByRegionalId(regionalId);
    }

    @GetMapping("regional/{regionalId}/uf/{ufId}")
    public List<UsuarioCidadeDto> getByIdRegionalAndIdUf(@PathVariable Integer regionalId,
                                                         @PathVariable Integer ufId) {
        return service.getAllByRegionalIdAndUfId(regionalId, ufId);
    }

    @GetMapping("grupo/{grupoId}")
    public List<UsuarioCidadeDto> getByIdGrupo(@PathVariable("grupoId") int grupoId) {
        return service.getAllByGrupoId(grupoId);
    }

    @GetMapping("cluster/{clusterId}")
    public List<UsuarioCidadeDto> getByIdCluster(@PathVariable("clusterId") int clusterId) {
        return service.getAllByClusterId(clusterId);
    }

    @GetMapping("sub-cluster/{subclusterId}")
    public List<UsuarioCidadeDto> getByIdSubCluster(@PathVariable("subclusterId") int subclusterId) {
        return service.getAllBySubClusterId(subclusterId);
    }

    @RequestMapping("comunicados")
    public List<UsuarioCidadeDto> getAtivosParaComunicados(@RequestParam Integer subclusterId) {
        return service.getAtivosParaComunicados(subclusterId);
    }

    @GetMapping("sub-clusters")
    public List<UsuarioCidadeDto> getByIdSubClusters(@RequestParam(name = "subclustersId") List<Integer> subclustersId) {
        return service.getAllBySubClustersId(subclustersId);
    }

    @GetMapping("cidade/{cidadeId}")
    public UsuarioCidadeDto getById(@PathVariable("cidadeId") Integer id) {
        return UsuarioCidadeDto.of(service.findById(id));
    }

    @GetMapping("{cidadeId}")
    public CidadeResponse getCidadeById(@PathVariable("cidadeId") Integer id) {
        return CidadeResponse.of(service.findById(id));
    }

    @GetMapping("{id}/clusterizacao")
    public ClusterizacaoDto getAll(@PathVariable Integer id) {
        return service.getClusterizacao(id);
    }

    @GetMapping("net-uno")
    public List<CidadeResponse> getAllCidadeNetUno() {
        return service.getAllCidadeNetUno();
    }

    @GetMapping("por-estados")
    public List<SelectResponse> buscarCidadesPorEstados(@RequestParam List<Integer> estadosIds) {
        return service.buscarCidadesPorEstadosIds(estadosIds);
    }

    @GetMapping("reprocessamento-regional")
    public List<UsuarioCidadeDto> buscarCidadesPorRegionalParaReprocessamento(@RequestParam Integer regionalId) {
        return service.getCidadesByRegionalReprocessamento(regionalId);
    }

    @GetMapping("reprocessamento-uf")
    public List<UsuarioCidadeDto> buscarCidadesPorRegionalAndUfParaReprocessamento(@RequestParam Integer regionalId,
                                                                                   @RequestParam Integer ufId) {
        return service.getCidadesByRegionalAndUfReprocessamento(regionalId, ufId);
    }

    @GetMapping("cidade-dbm/{codigoCidadeDbm}")
    public CidadeSiteResponse getCidadeByCodigoCidadeDbm(@PathVariable Integer codigoCidadeDbm) {
        return service.getCidadeByCodigoCidadeDbm(codigoCidadeDbm);
    }

    @GetMapping(params = "ufIds")
    public List<CidadeUfResponse> getAllCidadeByUfs(@RequestParam(name = "ufIds") List<Integer> ufIds) {
        return service.getAllCidadeByUfs(ufIds);
    }

    @GetMapping("uf-cidade-ids/{uf}/{cidade}")
    public CidadeUfResponse buscarCidadeUfIds(@PathVariable String uf, @PathVariable String cidade) {
        return CidadeUfResponse.of(service.findByUfNomeAndCidadeNome(uf, cidade));
    }

    @GetMapping("codigo-ibge/{codigoIbge}")
    public CidadeResponse findCidadeByCodigoIbge(@PathVariable String codigoIbge) {
        return service.findCidadeByCodigoIbge(codigoIbge);
    }

    @PostMapping("codigos-ibge")
    public List<CidadeResponse> findCidadesByCodigosIbge(@RequestBody List<String> codigosIbge) {
        return service.findCidadesByCodigosIbge(codigosIbge);
    }

    @GetMapping("estado-cidade-ids/{uf}/{cidade}")
    public CidadeResponse buscarCidadeEstadoIds(@PathVariable String uf, @PathVariable String cidade) {
        return CidadeResponse.of(service.findFirstByUfNomeAndCidadeNome(uf, cidade));
    }

    @PostMapping("por-nome-e-ufs")
    public List<CodigoIbgeRegionalResponse> findCodigoIbgeRegionalByCidadeNomeAndUf(
        @RequestBody CidadesUfsRequest cidadesUfs) {
        return service.getCodigoIbgeRegionalByCidadeNomeAndUf(cidadesUfs);
    }

    @GetMapping("codigo-ibge/regional")
    public List<CodigoIbgeRegionalResponse> getCodigoIbgeRegionalByCidade(@RequestParam(name = "cidadesId")
                                                                               List<Integer> cidadesId) {
        return service.getCodigoIbgeRegionalByCidade(cidadesId);
    }
}
