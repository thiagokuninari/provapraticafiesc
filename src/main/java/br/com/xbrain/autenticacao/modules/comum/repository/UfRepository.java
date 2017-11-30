package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UfRepository extends PagingAndSortingRepository<Uf, Integer> {

    @Cacheable("ifFindAll")
    Iterable<Uf> findAll(Sort var1);
}
