package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCidadeDto;

import java.util.List;

public interface UsuarioCidadeRepositoryCustom {
    List<Integer> findCidadesIdByUsuarioId(int usuarioId);

    List<UsuarioCidadeDto> findCidadesDtoByUsuarioId(Integer usuarioId);
}
