package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioFiltrosHierarquia;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import com.querydsl.core.types.Predicate;

import java.util.List;
import java.util.Optional;


public interface UsuarioRepositoryCustom {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findComplete(Integer id);

    Optional<Usuario> findComHierarquia(Integer id);

    Optional<Usuario> findComCidade(Integer id);

    List<Integer> getUsuariosSubordinados(Integer usuarioId);

    List<Usuario> getUsuariosFilter(Predicate predicate);

    List<Object[]> getUsuariosSuperiores(UsuarioFiltrosHierarquia filtros);

    Optional<UsuarioHierarquia> getUsuarioSuperior(Integer usuarioId);

    List<Usuario> getUsuarioByNivel(CodigoNivel codigoNivel);
}