package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SolicitacaoRamalRepositoryCustom {

    Page<SolicitacaoRamal> findAll(Pageable pageable, Predicate predicate);

    Page<SolicitacaoRamal> findAllGerenciaAa(Pageable pageable, Predicate predicate);

    Page<SolicitacaoRamal> findAllGerenciaD2d(Pageable pageable, Predicate predicate);

    List<SolicitacaoRamal> findAllBySituacaoAndDataEnviadoEmailExpiracaoIsNull();

    List<SolicitacaoRamal> findAllByAgenteAutorizadoIdAndSituacaoPendenteOuEmAndamento(Integer aaId);

    List<SolicitacaoRamal> findAllBySubCanalIdAndSituacaoPendenteOuEmAndamento(Integer subCanalId);

    List<SolicitacaoRamal> findAllByAgenteAutorizadoIdAndSituacaoEnviadoOuConcluido(Integer aaId);

    Optional<SolicitacaoRamal> findBySolicitacaoId(Integer solicitacaoId);

    List<SolicitacaoRamal> findAllByPredicate(Predicate predicate);
}
