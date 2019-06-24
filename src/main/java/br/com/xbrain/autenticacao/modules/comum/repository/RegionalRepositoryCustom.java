package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface RegionalRepositoryCustom {

    List<Regional> getAll(Predicate predicate);

    List<Regional> getAllByUsuarioId(Integer usuarioId);
}
