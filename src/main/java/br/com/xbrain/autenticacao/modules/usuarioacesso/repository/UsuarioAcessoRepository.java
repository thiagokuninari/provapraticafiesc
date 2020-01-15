package br.com.xbrain.autenticacao.modules.usuarioacesso.repository;

import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UsuarioAcessoRepository
    extends UsuarioAcessoRepositoryCustom,
    PagingAndSortingRepository<UsuarioAcesso, Integer>,
    QueryDslPredicateExecutor<UsuarioAcesso> {

    List<UsuarioAcesso> findAllByUsuarioId(Integer usuarioId);
}
