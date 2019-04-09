package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHistoricoDto;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHistorico;
import com.querydsl.core.types.Predicate;

import java.util.List;
import java.util.Optional;

public interface UsuarioHistoricoRepositoryCustom {

    Optional<UsuarioHistorico> getUltimoHistoricoPorUsuario(Integer usuarioId);
    
    List<UsuarioHistoricoDto> getHistoricoDoUsuario(Integer usuarioId);

    List<Usuario> getUsuariosPorTempoDeInatividade(Predicate predicate);

    List<UsuarioHistorico> findAllCompleteByUsuarioId(Integer usuarioid);
    
}
