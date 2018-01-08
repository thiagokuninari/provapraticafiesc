package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeAutoCompleteDto;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import com.querydsl.core.types.Predicate;

import java.util.List;
import java.util.Optional;

public interface CidadeRepositoryCustom {

    Iterable<Cidade> findBySubCluster(Integer subClusterId);

    List<Cidade> findAllByRegionalId(Integer regionalId);

    List<Cidade> findAllBySubClusterId(Integer subClusterId);

    List<Cidade> findAllByGrupoId(Integer grupoId);

    List<Cidade> findAllByClusterId(Integer clusterId);

    Optional<Cidade> findByPredicate(Predicate predicate);

}