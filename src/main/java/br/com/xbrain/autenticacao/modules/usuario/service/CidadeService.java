package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.comum.dto.ValidacaoException;
import br.com.xbrain.autenticacao.modules.permissao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCidadeDto;
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

    public List<UsuarioCidadeDto> getAllByRegionalId(Integer regionalId) {
        return UsuarioCidadeDto.parse(repository.findAllByRegionalId(regionalId));
    }

    public List<UsuarioCidadeDto> getAllBySubClusterId(Integer subClusterId) {
        return UsuarioCidadeDto.parse(repository.findAllBySubClusterId(subClusterId));
    }

    public List<UsuarioCidadeDto> getAllByGrupoId(Integer grupoId) {
        return UsuarioCidadeDto.parse(repository.findAllByGrupoId(grupoId));
    }

    public List<UsuarioCidadeDto> getAllByClusterId(Integer clusterId) {
        return UsuarioCidadeDto.parse(repository.findAllByClusterId(clusterId));
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
