package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;

import java.util.List;
import java.util.Optional;

public interface UsuarioHistoricoRepositoryCustom {

    Optional<UsuarioHistorico> getUltimoHistoricoPorUsuario(Integer usuarioId);
    
    List<UsuarioHistorico> getHistoricoDoUsuario(Integer usuarioId);

    List<UsuarioHistorico> findAllCompleteByUsuarioId(Integer usuarioid);

    void inativarUsuarioHistoricoGeradorLead(List<Integer> usuariosIds);
}
