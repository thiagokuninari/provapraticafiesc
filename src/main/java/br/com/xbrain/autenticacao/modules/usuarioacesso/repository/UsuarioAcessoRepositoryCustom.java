package br.com.xbrain.autenticacao.modules.usuarioacesso.repository;

import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioAcessoColaboradorResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface UsuarioAcessoRepositoryCustom {

    List<UsuarioAcesso> findAllUltimoAcessoUsuarios();

    List<UsuarioAcessoColaboradorResponse> findAllColaboradores(Predicate predicate);

    long deletarHistoricoUsuarioAcesso();

    long countUsuarioAcesso();
}
