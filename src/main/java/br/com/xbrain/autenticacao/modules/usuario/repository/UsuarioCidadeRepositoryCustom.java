package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidade;

import java.util.List;
import java.util.Set;

public interface UsuarioCidadeRepositoryCustom {

    List<Integer> findCidadesIdByUsuarioId(int usuarioId);

    List<Integer> findCidadesIdByUsuarioIdComDataBaixaNull(Integer usuarioId);

    Set<UsuarioCidade> findUsuarioCidadesByUsuarioId(Integer usuarioId);
}
