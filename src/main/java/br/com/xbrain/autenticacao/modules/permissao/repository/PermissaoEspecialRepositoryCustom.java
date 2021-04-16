package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;

import java.util.List;

public interface PermissaoEspecialRepositoryCustom {

    List<Funcionalidade> findPorUsuario(int usuarioId);

    void deletarPermissaoEspecialBy(List<Integer> funcionalidadeIds, List<Integer> usuarioIds);

}
