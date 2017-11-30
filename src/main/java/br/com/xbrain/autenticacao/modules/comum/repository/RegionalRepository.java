package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RegionalRepository extends PagingAndSortingRepository<Regional, Integer> {

    @Cacheable("regionalFindBySituacao")
    Iterable<Regional> findBySituacao(ESituacao situacao);
}