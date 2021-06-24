package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/cidades")
public class CidadeController {

    @Autowired
    private CidadeRepository repository;
    @Autowired
    private CidadeService service;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Cidade> get(Integer idUf, Integer idSubCluster) {
        if (idUf != null) {
            return service.getAllCidadeByUf(idUf);
        }
        if (idSubCluster != null) {
            return service.getAllBySubCluster(idSubCluster);
        }
        return Collections.emptyList();
    }

    @RequestMapping(value = "uf-cidade/{uf}/{cidade}", method = RequestMethod.GET)
    public CidadeResponse getByUfAndNome(@PathVariable("uf") String uf, @PathVariable("cidade") String cidade) {
        return CidadeResponse.parse(service.findByUfNomeAndCidadeNome(uf, cidade));
    }

    @GetMapping("recuperar-cidade/{uf}/{cidade}")
    public CidadeSubClusterResponse getCidadeSubcluster(@PathVariable String uf, @PathVariable String cidade) {
        return CidadeSubClusterResponse.parse(service.findByUfNomeAndCidadeNome(uf, cidade));
    }

    @RequestMapping("regional/{regionalId}")
    public List<UsuarioCidadeDto> getByIdRegional(@PathVariable("regionalId") int regionalId) {
        return service.getAllByRegionalId(regionalId);
    }

    @RequestMapping("grupo/{grupoId}")
    public List<UsuarioCidadeDto> getByIdGrupo(@PathVariable("grupoId") int grupoId) {
        return service.getAllByGrupoId(grupoId);
    }

    @RequestMapping("cluster/{clusterId}")
    public List<UsuarioCidadeDto> getByIdCluster(@PathVariable("clusterId") int clusterId) {
        return service.getAllByClusterId(clusterId);
    }

    @RequestMapping("sub-cluster/{subclusterId}")
    public List<UsuarioCidadeDto> getByIdSubCluster(@PathVariable("subclusterId") int subclusterId) {
        return service.getAllBySubClusterId(subclusterId);
    }

    @RequestMapping("comunicados")
    public List<UsuarioCidadeDto> getAtivosParaComunicados(@RequestParam Integer subclusterId) {
        return service.getAtivosParaComunicados(subclusterId);
    }

    @RequestMapping("sub-clusters")
    public List<UsuarioCidadeDto> getByIdSubClusters(@RequestParam(name = "subclustersId") List<Integer> subclustersId) {
        return service.getAllBySubClustersId(subclustersId);
    }

    @RequestMapping(value = "cidade/{cidadeId}")
    public UsuarioCidadeDto getById(@PathVariable("cidadeId") Integer id) {
        return UsuarioCidadeDto.of(repository.findOne(id));
    }

    @RequestMapping(value = "{cidadeId}")
    public CidadeResponse getCidadeById(@PathVariable("cidadeId") Integer id) {
        return CidadeResponse.parse(repository.findOne(id));
    }

    @GetMapping("{id}/clusterizacao")
    public ClusterizacaoDto getAll(@PathVariable Integer id) {
        return service.getClusterizacao(id);
    }

    @GetMapping("net-uno")
    public List<CidadeResponse> getAllCidadeNetUno() {
        return repository.findAllByNetUno(Eboolean.V).stream().map(CidadeResponse::parse).collect(Collectors.toList());
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
}
