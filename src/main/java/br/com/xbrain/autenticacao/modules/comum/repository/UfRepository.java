package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UfRepository extends CrudRepository<Uf, Integer>, UfRepositoryCustom {

    @Cacheable("ifFindAll")
    Iterable<Uf> findAll(Sort var1);

    List<Uf> findByOrderByNomeAsc();
}
