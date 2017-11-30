package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoMarca;
import br.com.xbrain.autenticacao.modules.comum.model.Marca;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MarcaRepository extends PagingAndSortingRepository<Marca, Integer> {

    @Cacheable("marcaFindByCodigo")
    Marca findByCodigo(CodigoMarca codigoMarca);

}
