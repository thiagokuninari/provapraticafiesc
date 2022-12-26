package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SolicitacaoRamalRepositoryCustom {

    Page<SolicitacaoRamal> findAll(Pageable pageable, Predicate predicate);

    PageImpl<SolicitacaoRamal> findAllGerencia(Pageable pageable, Predicate predicate,
                                               Predicate solicitacaoRamalPredicate);

    List<SolicitacaoRamal> findAllBySituacaoAndDataEnviadoEmailExpiracaoIsNull();

    List<SolicitacaoRamal> findAllByAgenteAutorizadoIdAndSituacaoPendenteOuEmAndamento(Integer aaId);

    List<SolicitacaoRamal> findAllBySubCanalIdAndSituacaoPendenteOuEmAndamento(Integer subCanalId);

    Optional<SolicitacaoRamal> findBySolicitacaoId(Integer solicitacaoId);

    List<SolicitacaoRamal> findAllByPredicate(Predicate predicate);
}
