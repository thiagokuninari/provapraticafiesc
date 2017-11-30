package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import com.querydsl.core.types.Predicate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EmpresaRepository extends PagingAndSortingRepository<Empresa, Integer>,
        QueryDslPredicateExecutor<Empresa> {

    @Cacheable("empresaFindAll")
    Iterable<Empresa> findAll(Predicate var1, Sort var2);
}