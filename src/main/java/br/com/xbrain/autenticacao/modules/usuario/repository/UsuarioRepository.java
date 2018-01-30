package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Integer>,
        QueryDslPredicateExecutor<Usuario>, UsuarioRepositoryCustom {

    Optional<Usuario> findTop1UsuarioByEmailIgnoreCase(String email);

    Optional<Usuario> findTop1UsuarioByCpf(String cpf);

    Optional<Usuario> findById(Integer id);

    Optional<Usuario> findByEmail(String email);

}
