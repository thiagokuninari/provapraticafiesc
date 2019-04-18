package br.com.xbrain.autenticacao.modules.usuario.repository;

import java.util.List;

public interface UsuarioCidadeRepositoryCustom {
    List<Integer> findCidadesIdByUsuarioId(int usuarioId);
}
