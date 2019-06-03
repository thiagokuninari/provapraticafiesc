package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import java.time.LocalDate;
import java.util.List;

public interface UsuarioFeriasRepositoryCustom {

    List<Usuario> getUsuariosInativosComFeriasEmAberto(LocalDate dataFimFerias);
}
