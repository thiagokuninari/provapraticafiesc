package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.predicate.SolicitacaoRamalPredicate;
import lombok.Data;

@Data
public class SolicitacaoRamalFiltros {

    private String dataInicialSolicitacao;
    private String dataFinalSolicitacao;
    private ESituacaoSolicitacao situacao;
    private Integer agenteAutorizadoId;
    private Integer page;
    private Integer size;

    public SolicitacaoRamalPredicate toPredicate() {
        return new SolicitacaoRamalPredicate()
                .comDataCadastro(this.dataInicialSolicitacao, this.dataFinalSolicitacao)
                .comSituacaoSolicitacao(this.situacao)
                .comAgenteAutorizadoId(this.agenteAutorizadoId);
    }
}
