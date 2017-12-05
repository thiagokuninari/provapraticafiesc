package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;

import java.util.List;

public interface CargoDepartamentoFuncionalidadeRepositoryCustom {

    List<CargoDepartamentoFuncionalidade> findFuncionalidadesPorCargoEDepartamento(Cargo cargo,
                                                                                  Departamento departamento);

}
