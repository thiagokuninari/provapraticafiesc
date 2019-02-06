package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SolicitacaoRamalRepositoryCustom {

    Page<SolicitacaoRamal> findAll(Pageable pageable, Predicate predicate);

    List<SolicitacaoRamal> findAllBySituacaoPendenteOrEmAndamentoAndEnviouEmailExpiracaoFalse();

}
