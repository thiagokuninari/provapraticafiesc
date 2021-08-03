package br.com.xbrain.autenticacao.modules.usuarioacesso.repository;

import br.com.xbrain.autenticacao.modules.usuarioacesso.model.UsuarioAcesso;

import java.time.LocalDateTime;
import java.util.List;

public interface UsuarioAcessoRepositoryCustom {

    List<UsuarioAcesso> findAllUltimoAcessoUsuarios(LocalDateTime dataHoraInativarUsuario);

    long deletarHistoricoUsuarioAcesso();

    long countUsuarioAcesso();
}
