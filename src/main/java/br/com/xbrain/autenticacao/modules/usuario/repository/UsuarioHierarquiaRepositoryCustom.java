package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;

import java.util.List;

public interface UsuarioHierarquiaRepositoryCustom {

    List<UsuarioHierarquia> findAllByIdUsuarioSuperior(Integer idUsuario);

    UsuarioHierarquia findByUsuarioHierarquia(Integer usuarioId, Integer supervisorId);
}