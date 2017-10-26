package br.com.xbrain.autenticacao.modules.permissao.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CargoDepartamentoFuncionalidadeRepository extends
        PagingAndSortingRepository<CargoDepartamentoFuncionalidade, Integer>,
        CargoDepartamentoFuncionalidadeRepositoryCustom {
}
