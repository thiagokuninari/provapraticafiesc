package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryCustom {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findComplete(Integer id);

    Optional<Usuario> findComHierarquia(Integer id);

    Optional<Usuario> findComCidade(Integer id);
}