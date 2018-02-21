package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioFiltrosHierarquia;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    List<UsuarioHierarquia> getUsuarioSuperiores(Integer usuarioId);

    List<PermissaoEspecial> getUsuariosByPermissao(CodigoFuncionalidade codigoFuncionalidade);

    List<Usuario> getUsuariosByNivel(CodigoNivel codigoNivel);

    List<Integer> getUsuariosPorCidade(Integer idUsuario);

    Page<Usuario> findAll(Predicate predicate, Pageable pageable);

}