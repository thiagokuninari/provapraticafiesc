package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.repository;

import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.ImportacaoFeriado;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ImportacaoAutomaticaFeriadoRepository extends PagingAndSortingRepository<ImportacaoFeriado, Integer>,
    QueryDslPredicateExecutor<ImportacaoFeriado> {

}
