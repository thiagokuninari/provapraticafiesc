package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UnidadeNegocioRepository extends PagingAndSortingRepository<UnidadeNegocio, Integer> {

    @Cacheable("unidadeNegocioFindAll")
    Iterable<UnidadeNegocio> findAll(Sort var1);

    @Cacheable("unidadeNegocioFindByNomeIsNot")
    Iterable<UnidadeNegocio> findByNomeIsNot(String nome, Sort sort);

}
