package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.predicate.FuncionalidadePredicate;

import java.util.List;

public interface CargoDepartamentoFuncionalidadeRepositoryCustom  {

    List<CargoDepartamentoFuncionalidade> findFuncionalidadesPorCargoEDepartamento(
            FuncionalidadePredicate predicate);
}
