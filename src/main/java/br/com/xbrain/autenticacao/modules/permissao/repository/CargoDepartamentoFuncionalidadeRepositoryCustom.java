package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CargoDepartamentoFuncionalidadeRepositoryCustom  {

    List<CargoDepartamentoFuncionalidade> findFuncionalidadesPorCargoEDepartamento(Predicate predicate);

    Page<CargoDepartamentoFuncionalidade> findAll(Predicate predicate, Pageable pageable);
}
