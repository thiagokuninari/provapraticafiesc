package br.com.xbrain.autenticacao.modules.feriado.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface FeriadoRepository extends PagingAndSortingRepository<Feriado, Integer>, FeriadoRepositoryCustom,
    QueryDslPredicateExecutor<Feriado> {

    Optional<Feriado> findByDataFeriadoAndFeriadoNacional(LocalDate data, Eboolean nacional);

    Optional<Feriado> findByDataFeriadoAndCidadeId(LocalDate data, Integer cidadeId);

    Optional<Feriado> findById(Integer id);
}
