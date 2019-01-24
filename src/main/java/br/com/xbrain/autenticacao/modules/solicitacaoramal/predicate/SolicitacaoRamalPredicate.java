package br.com.xbrain.autenticacao.modules.solicitacaoramal.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
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

    public SolicitacaoRamalPredicate comDataCadastro(LocalDate data) {
        if (!ObjectUtils.isEmpty(data)) {
            builder.and(solicitacaoRamal.dataCadastro.between(
                    LocalDateTime.of(data, LocalTime.MIN),
                    LocalDateTime.of(data, LocalTime.MAX)));
        }

        return this;
    }
}
