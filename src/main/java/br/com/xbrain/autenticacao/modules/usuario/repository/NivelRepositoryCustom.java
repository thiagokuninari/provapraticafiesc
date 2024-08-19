package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface NivelRepositoryCustom {

    List<Nivel> getAll(Predicate predicate);
}
