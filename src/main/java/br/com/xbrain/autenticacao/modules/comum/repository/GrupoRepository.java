package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Grupo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GrupoRepository extends PagingAndSortingRepository<Grupo, Integer> {

    @Cacheable("grupoFindBySituacaoAndRegionalId")
    Iterable<Grupo> findBySituacaoAndRegionalId(ESituacao situacao, Integer regionalId, Sort sort);

    @Cacheable("grupoFindBySituacao")
    Iterable<Grupo> findBySituacao(ESituacao situacao, Sort sort);
}
