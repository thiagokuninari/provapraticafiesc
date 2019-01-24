package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.comum.util.DateUtil;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.predicate.SolicitacaoRamalPredicate;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SolicitacaoRamalFiltros {

    private String data;
    private ESituacao situacao;

    public SolicitacaoRamalPredicate toPredicate() {
        LocalDate data = DateUtil.parseStringToLocalDateDefault(this.data);

        return new SolicitacaoRamalPredicate()
                .comDataCadastro(data)
                .comSituacao(this.situacao);
    }
}
