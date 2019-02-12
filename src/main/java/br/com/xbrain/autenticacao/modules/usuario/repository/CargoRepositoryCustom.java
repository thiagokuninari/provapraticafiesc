package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import com.querydsl.core.types.Predicate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CargoRepositoryCustom {

    Page<Cargo> findAll(Predicate predicate, Pageable pageable);

    List<Cargo> findAll(Predicate predicate);

    @Cacheable("cargoFindBySituacaoAndNivelId")
    Iterable<Cargo> findBySituacaoAndNivelId(ESituacao situacao, Integer nivelId);
}
