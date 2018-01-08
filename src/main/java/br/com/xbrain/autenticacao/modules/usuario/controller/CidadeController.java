package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping(value = "api/cidades")
public class CidadeController {

    @Autowired
    private CidadeRepository repository;
    @Autowired
    private CidadeService cidadeService;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Cidade> get(Integer idUf, Integer idSubCluster) {
        if (idUf != null) {
            return repository.findCidadeByUfId(idUf, new Sort("nome"));
        }
        if (idSubCluster != null) {
            return repository.findBySubCluster(idSubCluster);
        }
        return Collections.emptyList();
    }

    @RequestMapping(value = "uf-cidade/{uf}/{cidade}", method = RequestMethod.GET)
    public CidadeResponse getByUfAndNome(@PathVariable("uf") String uf,
                                         @PathVariable("cidade") String cidade) {
        return CidadeResponse.parse(cidadeService.findByUfNomeAndCidadeNome(uf, cidade));
    }

    @RequestMapping("regional/{regionalId}")
    public Iterable<Cidade> getByIdRegional(@PathVariable("regionalId") int regionalId) {
        return repository.findByRegional(regionalId);
    }

    @RequestMapping("grupo/{grupoId}")
    public Iterable<Cidade> getByIdGrupo(@PathVariable("grupoId") int grupoId) {
        return repository.findByGrupo(grupoId);
    }

    @RequestMapping("cluster/{clusterId}")
    public Iterable<Cidade> getByIdCluster(@PathVariable("clusterId") int clusterId) {
        return repository.findByCluster(clusterId);
    }

    @RequestMapping("sub-cluster/{subclusterId}")
    public Iterable<Cidade> getByIdSubCluster(@PathVariable("subclusterId") int subclusterId) {
        return repository.findBySubCluster(subclusterId);
    }

    @RequestMapping("usuario/{usuarioId}")
    public Iterable<Cidade> getByIdUsuario(@PathVariable("usuarioId") int usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }
}