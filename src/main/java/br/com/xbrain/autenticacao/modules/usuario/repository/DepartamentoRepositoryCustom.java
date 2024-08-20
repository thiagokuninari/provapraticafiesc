package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface DepartamentoRepositoryCustom {

    List<Departamento> findAll(Predicate predicate);
}
