package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
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

    @Autowired
    private UsuarioService usuarioService;

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
    public CidadeResponse getByUfAndNome(@PathVariable("uf") String uf,
                                         @PathVariable("cidade") String cidade) {
        return CidadeResponse.parse(service.findByUfNomeAndCidadeNome(uf, cidade));
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

    @RequestMapping("sub-clusters")
    public List<UsuarioCidadeDto> getByIdSubClusters(@RequestParam(name = "subclustersId") List<Integer> subclustersId) {
        return service.getAllBySubClustersId(subclustersId);
    }

    @RequestMapping(value = "cidade/{cidadeId}")
    public UsuarioCidadeDto getById(@PathVariable("cidadeId") Integer id) {
        return UsuarioCidadeDto.parse(repository.findOne(id));
    }

    @RequestMapping(value = "{cidadeId}")
    public CidadeResponse getCidadeById(@PathVariable("cidadeId") Integer id) {
        return CidadeResponse.parse(repository.findOne(id));
    }

    @GetMapping("/supervisores")
    public List<UsuarioEquipeVendasResponse> getSupervisoresByCidades(@RequestParam(name = "cidadesId") List<Integer> cidadesId) {
        return usuarioService.getSupervisoresByCidades(cidadesId).stream()
                .map(UsuarioEquipeVendasResponse::convertFrom)
                .collect(Collectors.toList());
    }

    @GetMapping("/usuarios")
    public List<UsuarioEquipeVendasResponse> getUsuariosByCidades(@RequestParam(name = "cidadesId") List<Integer> cidadesId) {
        return usuarioService.getUsuariosByCidades(cidadesId).stream()
                .map(UsuarioEquipeVendasResponse::convertFrom)
                .collect(Collectors.toList());
    }

    @GetMapping("supervisor/usuarios/{id}")
    public List<UsuarioResponse> getUsuariosBySupervisor(@PathVariable Integer id) {
        return usuarioService.getUsuariosBySupervisor(id);
    }

    @GetMapping("cidades/supervisores/{hierarquia}/{id}")
    public List<UsuarioResponse> getSupervisoresByHierarquia(@PathVariable String hierarquia, @PathVariable Integer id) {
        return service.getSupervisoresByHierarquia(hierarquia, id);
    }

    @GetMapping("{id}/clusterizacao")
    public ClusterizacaoDto getAll(@PathVariable Integer id) {
        return service.getClusterizacao(id);
    }

    @GetMapping("net-uno")
    public List<CidadeResponse> getAllCidadeNetUno() {
        return repository.findAllByNetUno(Eboolean.V).stream().map(CidadeResponse::parse).collect(Collectors.toList());
    }
}