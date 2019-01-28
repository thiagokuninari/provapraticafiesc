package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.comum.util.DateUtil;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.predicate.SolicitacaoRamalPredicate;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SolicitacaoRamalFiltros {

    @JsonFormat(pattern = "dd/MM/yyyy")
    private String data;
    private ESituacao situacao;

    public SolicitacaoRamalPredicate toPredicate() {
        LocalDate data = DateUtil.parseStringToLocalDate(this.data);

        return new SolicitacaoRamalPredicate()
                .comDataCadastro(data)
                .comSituacao(this.situacao);
    }
}
