package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.predicate.SolicitacaoRamalPredicate;
import lombok.Data;

@Data
public class SolicitacaoRamalFiltros {

    private String data;
    private ESituacao situacao;

    public SolicitacaoRamalPredicate toPredicate() {
        return new SolicitacaoRamalPredicate()
                .comDataCadastro(data)
                .comSituacao(this.situacao);
    }
}
