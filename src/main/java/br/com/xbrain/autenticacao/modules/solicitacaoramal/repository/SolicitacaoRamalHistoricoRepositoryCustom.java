package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;

import java.util.List;

public interface SolicitacaoRamalHistoricoRepositoryCustom {

    List<SolicitacaoRamalHistorico> findAllBySolicitacaoRamalId(Integer id);

    void deleteAll(Integer solicitacaoId);
}
