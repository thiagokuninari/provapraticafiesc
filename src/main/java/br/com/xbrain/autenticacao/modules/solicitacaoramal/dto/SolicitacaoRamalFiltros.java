package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.comum.util.DateUtil;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.predicate.SolicitacaoRamalPredicate;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Data
public class SolicitacaoRamalFiltros {

    private String data;
    private ESituacao situacao;

    public SolicitacaoRamalPredicate toPredicate() {
        try {
            LocalDate data = DateUtil.parseStringToLocalDate(this.data);

            return new SolicitacaoRamalPredicate()
                    .comDataCadastro(data)
                    .comSituacao(this.situacao);
        } catch (DateTimeParseException ex) {
            throw new RuntimeException("Não foi possível converter a data: " + this.data);
        }
    }
}
