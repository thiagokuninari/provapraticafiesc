package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidade;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UsuarioRepositoryCustom {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findComplete(Integer id);

    Optional<Usuario> findComHierarquia(Integer id);

    Optional<Usuario> findComCidade(Integer id);

}