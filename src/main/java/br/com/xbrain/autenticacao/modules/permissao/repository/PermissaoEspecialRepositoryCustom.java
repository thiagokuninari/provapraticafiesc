package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface PermissaoEspecialRepositoryCustom {

    List<Funcionalidade> findPorUsuario(int usuarioId);

    Optional<Usuario> findUsuarioComPermissaoEspecial(CodigoFuncionalidade codigoFuncionalidade);
}
