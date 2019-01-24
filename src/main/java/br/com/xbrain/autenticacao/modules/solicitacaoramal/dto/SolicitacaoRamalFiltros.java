package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.predicate.SolicitacaoRamalPredicate;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
public class SolicitacaoRamalFiltros {

    private String data;
    private ESituacao situacao;

    public SolicitacaoRamalPredicate toPredicate() {
        LocalDate data = validarFiltroData();

        return new SolicitacaoRamalPredicate()
                .comDataCadastro(data)
                .comSituacao(this.situacao);
    }

    private LocalDate validarFiltroData() {
        if (!ObjectUtils.isEmpty(this.data)) {
            try {
                return LocalDate.parse(this.data, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException ex) {
                throw new RuntimeException("Não foi possível converter a data: "
                        + this.data + " favor utilizar o padrão yyyy-MM-dd");
            }
        }

        return null;
    }
}
