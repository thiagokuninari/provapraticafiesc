package br.com.xbrain.autenticacao.modules.usuarioacesso.repository;

import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;
import com.querydsl.core.Tuple;

import java.util.List;

public interface UsuarioAcessoRepositoryCustom {

    List<UsuarioAcesso> findAllUltimoAcessoUsuarios();
}
