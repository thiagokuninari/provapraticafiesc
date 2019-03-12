package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.dto.ClusterizacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCidadeDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoHierarquia;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/cidades")
public class CidadeController {

    @Autowired
    private CidadeRepository repository;
    private static final Integer MAXIMO = 1000;
    private static final Integer MINIMO = 1001;


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

    @GetMapping("usuarios-cidades/{hierarquia}/{id}")
    public List<UsuarioResponse> getUsuariosByCidades(@PathVariable String hierarquia, @PathVariable Integer id) {
        List<UsuarioCidadeDto> cidades = null;
        if (hierarquia.equalsIgnoreCase(CodigoHierarquia.REGIONAL.name())) {
            cidades = service.getAllByRegionalId(id);
        } else if (hierarquia.equalsIgnoreCase(CodigoHierarquia.GRUPO.name())) {
            cidades = service.getAllByGrupoId(id);
        } else if (hierarquia.equalsIgnoreCase(CodigoHierarquia.CLUSTER.name())) {
            cidades = service.getAllByClusterId(id);
        } else if (hierarquia.equalsIgnoreCase(CodigoHierarquia.SUBCLUSTER.name())) {
            cidades = service.getAllBySubClusterId(id);
        } else if (hierarquia.equalsIgnoreCase(CodigoHierarquia.CIDADE.name())) {
            return usuarioService.getUsuariosByCidades(Arrays.asList(id));
        }
        List<Integer> cidadesId = cidades.stream()
                .map(usuarioCidade -> usuarioCidade.getIdCidade())
                .collect(Collectors.toList());
        if (cidadesId.size() > MAXIMO) {
            List<Integer> segundaLista = cidadesId.subList(MINIMO, cidadesId.size());
            cidadesId = cidadesId.subList(0, MAXIMO);
            List<UsuarioResponse> listaUsuario = usuarioService.getUsuariosByCidades(segundaLista);
            List<UsuarioResponse> listaUsuarioComplementar = usuarioService.getUsuariosByCidades(cidadesId);
            listaUsuario.addAll(listaUsuarioComplementar);
            return listaUsuario;
        }

        return usuarioService.getUsuariosByCidades(cidadesId);

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