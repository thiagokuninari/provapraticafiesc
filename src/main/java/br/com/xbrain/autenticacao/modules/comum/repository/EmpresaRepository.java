package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import com.querydsl.core.types.Predicate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;

public interface EmpresaRepository extends PagingAndSortingRepository<Empresa, Integer>,
        QueryDslPredicateExecutor<Empresa> {

    List<Empresa> findAll();

    @Cacheable("empresaFindAll")
    Iterable<Empresa> findAll(Predicate var1, Sort var2);

    @Cacheable("empresaFindAllAtivo")
    @Query("SELECT e from Empresa e where e.situacao = 'A'")
    List<Empresa> findAllAtivo();

    List<Empresa> findByCodigoIn(Collection<CodigoEmpresa> codigos);

}
