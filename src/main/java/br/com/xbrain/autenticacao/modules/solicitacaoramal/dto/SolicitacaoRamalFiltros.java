package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.predicate.SolicitacaoRamalPredicate;
import lombok.Data;

@Data
public class SolicitacaoRamalFiltros {

    private String data;
    private ESituacaoSolicitacao situacao;
    private Integer agenteAutorizadoId;

    public SolicitacaoRamalPredicate toPredicate() {
        return new SolicitacaoRamalPredicate()
                .comDataCadastro(this.data)
                .comSituacaoSolicitacao(this.situacao)
                .comAgenteAutorizadoId(this.agenteAutorizadoId);
    }
}
