package br.com.xbrain.autenticacao.modules.feriado.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import com.querydsl.core.types.Predicate;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FeriadoRepository extends PagingAndSortingRepository<Feriado, Integer>, FeriadoRepositoryCustom,
    QueryDslPredicateExecutor<Feriado> {

    Optional<Feriado> findByDataFeriadoAndFeriadoNacionalAndSituacao(LocalDate data, Eboolean nacional,
                                                                     ESituacaoFeriado situacao);

    Optional<Feriado> findByDataFeriadoAndCidadeIdAndSituacao(LocalDate data, Integer cidadeId, ESituacaoFeriado situacao);

    Optional<Feriado> findById(Integer id);

    List<Feriado> findAll(Predicate predicate);

    boolean hasFeriadoByCidadeIdAndDataAtual(Integer cidadeId, LocalDate dataAtual);
}
