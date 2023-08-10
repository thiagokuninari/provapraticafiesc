package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface RegionalRepository extends PagingAndSortingRepository<Regional, Integer>, RegionalRepositoryCustom {

    @Cacheable("regionalFindBySituacao")
    Iterable<Regional> findBySituacao(ESituacao situacao);

    Optional<Regional> findById(Integer id);

    List<Regional> findAllBySituacaoAndNovaRegional(ESituacao situacao, Eboolean novaRegional);
}
