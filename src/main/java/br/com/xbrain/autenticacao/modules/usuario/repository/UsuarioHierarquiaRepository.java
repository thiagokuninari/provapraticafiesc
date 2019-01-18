package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface UsuarioHierarquiaRepository extends PagingAndSortingRepository<UsuarioHierarquia, Integer>,
        QueryDslPredicateExecutor<UsuarioHierarquia> {
    Set<UsuarioHierarquia> findByUsuarioIdIn(List<Integer> hierarquiasId);

    @Query("FROM UsuarioHierarquia WHERE usuario.id = :id")
    UsuarioHierarquia findByIdUsuario(@Param("id") Integer id);
}
