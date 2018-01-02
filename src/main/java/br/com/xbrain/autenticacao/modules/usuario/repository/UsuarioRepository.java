package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Integer>,
        QueryDslPredicateExecutor<Usuario>, UsuarioRepositoryCustom {

    Optional<Usuario> findById(Integer id);

    Optional<Usuario> findByCpf(String cpf);

    Optional<Usuario> findByEmail(String email);

}
