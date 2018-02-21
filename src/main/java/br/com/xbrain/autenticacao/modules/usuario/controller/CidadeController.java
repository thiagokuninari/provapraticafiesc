package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCidadeDto;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

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

    @RequestMapping(value = "cidade/{cidadeId}")
    public UsuarioCidadeDto getById(@PathVariable("cidadeId") Integer id) {
        return UsuarioCidadeDto.parse(repository.findOne(id));
    }
}