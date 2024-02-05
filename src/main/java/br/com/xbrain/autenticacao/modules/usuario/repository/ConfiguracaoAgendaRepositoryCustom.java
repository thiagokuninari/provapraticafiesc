package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgenda;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ConfiguracaoAgendaRepositoryCustom {

    Optional<ConfiguracaoAgenda> findByPredicateOrderByQtdHorasDesc(Predicate predicate);

    Page<ConfiguracaoAgenda> findAllByPredicate(Predicate predicate, PageRequest pageable);
}
