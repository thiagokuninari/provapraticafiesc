package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalDadosAdicionaisResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalResponse;

public interface ISolicitacaoRamalService {

    SolicitacaoRamalResponse save(SolicitacaoRamalRequest request);

    SolicitacaoRamalDadosAdicionaisResponse getDadosAdicionais(Integer id);

    SolicitacaoRamalResponse update(SolicitacaoRamalRequest request);
}
