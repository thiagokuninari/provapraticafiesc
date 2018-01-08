package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.dto.ValidacaoException;
import br.com.xbrain.autenticacao.modules.permissao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CidadePredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CidadeService {

    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Cidade não encontrada.");

    @Getter
    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private CidadeRepository repository;

    public List<Cidade> getAllByRegionalId(Integer regionalId) {
        return repository.findAllByRegionalId(regionalId);
    }

    public List<Cidade> getAllBySubClusterId(Integer subClusterId) {
        return repository.findAllBySubClusterId(subClusterId);
    }

    public List<Cidade> getAllByGrupoId(Integer grupoId) {
        return repository.findAllByGrupoId(grupoId);
    }

    public List<Cidade> getAllByClusterId(Integer clusterId) {
        return repository.findAllByClusterId(clusterId);
    }

    public List<Cidade> getAllCidadeByUf(Integer idUf) {
        return repository.findCidadeByUfId(idUf, new Sort("nome"));
    }

    public List<Cidade> getAllBySubCluster(Integer idSubCluster) {
        return repository.findBySubCluster(idSubCluster);
    }

    public Cidade findByUfNomeAndCidadeNome(String uf, String cidade) {
        CidadePredicate predicate = new CidadePredicate();
        predicate.comNome(cidade);
        predicate.comUf(uf);
        return repository
                .findByPredicate(predicate.build())
                .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }
}
