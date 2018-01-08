package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeAutoCompleteDto;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import com.querydsl.core.types.Predicate;

import java.util.List;
import java.util.Optional;

public interface CidadeRepositoryCustom {

    List<CidadeAutoCompleteDto> findAllAtivas();

    Iterable<Cidade> findBySubCluster(Integer subClusterId);

    Iterable<Cidade> findByRegional(Integer regionalId);

    Iterable<Cidade> findByGrupo(Integer grupoId);

    Iterable<Cidade> findByCluster(Integer clusterId);

    Optional<Cidade> findByPredicate(Predicate predicate);

    List<String> findByUf(String uf);

    List<Cidade> find(Predicate predicate);

    Iterable<Cidade> findByUsuarioId(Integer usuarioId);

}