package br.com.xbrain.autenticacao.modules.solicitacaoramal.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.comum.util.DateUtil;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacao;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.model.QSolicitacaoRamal.solicitacaoRamal;

public class SolicitacaoRamalPredicate extends PredicateBase {

    public SolicitacaoRamalPredicate comSituacao(String situacao) {
        if (!ObjectUtils.isEmpty(situacao)) {
            Optional<ESituacao> situacaoEnum = Arrays.stream(ESituacao.values())
                    .filter(s -> s.getDescricao().equals(situacao)).findFirst();

            builder.and(solicitacaoRamal.situacao.eq(situacaoEnum.get()));
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

    public SolicitacaoRamalPredicate comAgenteAutorizadoId(Integer agenteAutorizadoId) {
        if (!ObjectUtils.isEmpty(agenteAutorizadoId)) {
            builder.and(solicitacaoRamal.agenteAutorizadoId.eq(agenteAutorizadoId));
        }

        return this;
    }
}
