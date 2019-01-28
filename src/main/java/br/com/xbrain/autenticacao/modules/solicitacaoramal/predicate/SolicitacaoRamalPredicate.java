package br.com.xbrain.autenticacao.modules.solicitacaoramal.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.comum.util.DateUtil;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacao;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.model.QSolicitacaoRamal.solicitacaoRamal;

public class SolicitacaoRamalPredicate extends PredicateBase {

    public SolicitacaoRamalPredicate comSituacao(ESituacao situacao) {
        if (!ObjectUtils.isEmpty(situacao)) {
            builder.and(solicitacaoRamal.situacao.eq(situacao));
        }

        return this;
    }

    public SolicitacaoRamalPredicate comDataCadastro(String data) {
        if (!ObjectUtils.isEmpty(data)) {
            LocalDate dataFormatada = DateUtil.parseStringToLocalDate(data);

            builder.and(solicitacaoRamal.dataCadastro.between(
                    LocalDateTime.of(dataFormatada, LocalTime.MIN),
                    LocalDateTime.of(dataFormatada, LocalTime.MAX)));
        }

        return this;
    }
}
