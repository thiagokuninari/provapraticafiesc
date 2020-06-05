package br.com.xbrain.autenticacao.modules.usuarioacesso.repository;

import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.PaLogadoResponse;
import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import com.querydsl.core.BooleanBuilder;

import java.util.List;

public interface UsuarioAcessoRepositoryCustom {

    List<UsuarioAcesso> findAllUltimoAcessoUsuarios();

    long deletarHistoricoUsuarioAcesso();

    long countUsuarioAcesso();

    List<PaLogadoResponse> getAllLoginByFiltros(BooleanBuilder predicate);
}
