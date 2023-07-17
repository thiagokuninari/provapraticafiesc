package br.com.xbrain.autenticacao.modules.solicitacaoramal.service;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalDadosAdicionaisResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalResponse;
import org.springframework.data.domain.Page;

public interface ISolicitacaoRamalService {

    SolicitacaoRamalResponse save(SolicitacaoRamalRequest request);

    SolicitacaoRamalDadosAdicionaisResponse getDadosAdicionais(SolicitacaoRamalFiltros filtros);

    SolicitacaoRamalResponse update(SolicitacaoRamalRequest request);

    Page<SolicitacaoRamalResponse> getAllGerencia(PageRequest pageable, SolicitacaoRamalFiltros filtros);
}
