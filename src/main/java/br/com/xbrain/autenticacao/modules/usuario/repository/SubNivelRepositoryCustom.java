package br.com.xbrain.autenticacao.modules.usuario.repository;

import java.util.List;

public interface SubNivelRepositoryCustom {

    List<Integer> findFuncionalidadesIdsByNivelId(Integer nivelId);
}
