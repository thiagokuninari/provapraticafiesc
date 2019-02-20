package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SolicitacaoRamalRepositoryCustom {

    Page<SolicitacaoRamal> findAll(Pageable pageable, Predicate predicate);

    PageImpl<SolicitacaoRamal> findAllGerencia(Pageable pageable, Predicate predicate, SolicitacaoRamalFiltros filtros);

    List<SolicitacaoRamal> findAllBySituacaoAndDataEnviadoEmailExpiracaoIsNull();

    List<SolicitacaoRamal> findAllByAgenteAutorizadoIdAndSituacaoDiferentePendenteOuEmAndamento(Integer aaId);

}
