package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;

public interface UnidadeNegocioRepository extends PagingAndSortingRepository<UnidadeNegocio, Integer>,
        UnidadeNegocioRepositoryCustom {

    List<UnidadeNegocio> findAll();

    @Cacheable("unidadeNegocioFindAll")
    Iterable<UnidadeNegocio> findAll(Sort var1);

    @Cacheable("unidadeNegocioFindByNomeIsNot")
    Iterable<UnidadeNegocio> findByNomeIsNot(String nome, Sort sort);

    @Cacheable("unidadeNegocioFindAllAtivo")
    @Query("SELECT un from UnidadeNegocio un where un.situacao = 'A'")
    List<UnidadeNegocio> findAllAtivo();

    List<UnidadeNegocio> findByCodigoIn(Collection<CodigoUnidadeNegocio> codigos);

}
