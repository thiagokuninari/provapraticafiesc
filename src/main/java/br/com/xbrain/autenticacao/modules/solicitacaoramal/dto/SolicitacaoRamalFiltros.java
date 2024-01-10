package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.predicate.SolicitacaoRamalPredicate;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import lombok.Data;

@Data
public class SolicitacaoRamalFiltros {

    private String dataInicialSolicitacao;
    private String dataFinalSolicitacao;
    private ESituacaoSolicitacao situacao;
    private Integer agenteAutorizadoId;
    private ECanal canal;
    private Integer subCanalId;
    private Integer equipeId;

    public SolicitacaoRamalPredicate toPredicate() {
        return new SolicitacaoRamalPredicate()
            .comDataCadastro(this.dataInicialSolicitacao, this.dataFinalSolicitacao)
            .comSituacaoSolicitacao(this.situacao)
            .comAgenteAutorizadoId(this.agenteAutorizadoId)
            .comCanal(this.canal)
            .comSubCanalId(this.subCanalId)
            .comEquipeId(equipeId);
    }
}
