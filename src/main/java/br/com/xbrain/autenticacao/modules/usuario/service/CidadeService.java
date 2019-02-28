package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.ClusterizacaoDto;
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
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        CidadePredicate predicate = new CidadePredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return UsuarioCidadeDto.parse(repository.findAllByRegionalId(regionalId, predicate.build()));
    }

    public List<UsuarioCidadeDto> getAllBySubClusterId(Integer subClusterId) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        CidadePredicate predicate = new CidadePredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return UsuarioCidadeDto.parse(repository.findAllBySubClusterId(subClusterId, predicate.build()));
    }

    public List<UsuarioCidadeDto> getAllBySubClustersId(List<Integer> subClustersId) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        CidadePredicate predicate = new CidadePredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return UsuarioCidadeDto.parse(repository.findAllBySubClustersId(subClustersId, predicate.build()));
    }

    public List<UsuarioCidadeDto> getAllByGrupoId(Integer grupoId) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        CidadePredicate predicate = new CidadePredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return UsuarioCidadeDto.parse(repository.findAllByGrupoId(grupoId, predicate.build()));
    }

    public List<UsuarioCidadeDto> getAllByClusterId(Integer clusterId) {
        UsuarioAutenticado usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        CidadePredicate predicate = new CidadePredicate();
        predicate.filtrarPermitidos(usuarioAutenticado);
        return UsuarioCidadeDto.parse(repository.findAllByClusterId(clusterId, predicate.build()));
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

    public ClusterizacaoDto getClusterizacao(Integer id) {
        return repository.getClusterizacao(id);
    }
}
