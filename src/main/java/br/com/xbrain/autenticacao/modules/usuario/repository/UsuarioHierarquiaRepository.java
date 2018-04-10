package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UsuarioHierarquiaRepository extends PagingAndSortingRepository<UsuarioHierarquia, Integer>,
        QueryDslPredicateExecutor<UsuarioHierarquia> {
}
