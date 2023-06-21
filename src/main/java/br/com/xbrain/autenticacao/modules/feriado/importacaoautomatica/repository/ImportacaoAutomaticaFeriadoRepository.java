package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.repository;

import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.ImportacaoFeriado;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ImportacaoAutomaticaFeriadoRepository extends PagingAndSortingRepository<ImportacaoFeriado, Integer>,
    QueryDslPredicateExecutor<ImportacaoFeriado> {

    Page<ImportacaoFeriado> findAll(Predicate predicate, Pageable pageable) ;

}
