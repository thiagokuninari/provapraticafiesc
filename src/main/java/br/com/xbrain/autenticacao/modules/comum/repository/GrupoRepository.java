package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Grupo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GrupoRepository extends PagingAndSortingRepository<Grupo, Integer>, GrupoRepositoryCustom {

    @Cacheable("grupoFindBySituacaoAndRegionalId")
    Iterable<Grupo> findBySituacaoAndRegionalId(ESituacao situacao, Integer regionalId, Sort sort);

    @Cacheable("grupoFindBySituacao")
    List<Grupo> findBySituacao(ESituacao situacao, Sort sort);
}
