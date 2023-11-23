package br.com.xbrain.autenticacao.modules.solicitacaoramal.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.xbrainutils.DateUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.model.QSolicitacaoRamal.solicitacaoRamal;

public class SolicitacaoRamalPredicate extends PredicateBase {

    public SolicitacaoRamalPredicate comSituacaoSolicitacao(ESituacaoSolicitacao situacao) {
        if (!ObjectUtils.isEmpty(situacao)) {
            builder.and(solicitacaoRamal.situacao.eq(situacao));
        }

        return this;
    }

    public SolicitacaoRamalPredicate comDataCadastro(String dataInicialSolicitacao, String dataFinalSolicitacao) {
        if (!ObjectUtils.isEmpty(dataInicialSolicitacao) && !ObjectUtils.isEmpty(dataFinalSolicitacao)) {
            builder.and(solicitacaoRamal.dataCadastro.between(
                LocalDateTime.of(DateUtils.parseStringToLocalDate(dataInicialSolicitacao), LocalTime.MIN),
                LocalDateTime.of(DateUtils.parseStringToLocalDate(dataFinalSolicitacao), LocalTime.MAX)));
        }

        return this;
    }

    public SolicitacaoRamalPredicate comDataFinalizacaoNula() {
        builder.and(solicitacaoRamal.dataFinalizacao.isNull());
        return this;
    }

    public SolicitacaoRamalPredicate comAgenteAutorizadoId(Integer agenteAutorizadoId) {
        if (!ObjectUtils.isEmpty(agenteAutorizadoId)) {
            builder.and(solicitacaoRamal.agenteAutorizadoId.eq(agenteAutorizadoId));
        }

        return this;
    }

    public SolicitacaoRamalPredicate comCanal(ECanal canal) {
        if (canal != null) {
            builder.and(solicitacaoRamal.canal.eq(canal));
        }

        return this;
    }

    public SolicitacaoRamalPredicate comSubCanalId(Integer subCanalId) {
        if (subCanalId != null) {
            builder.and(solicitacaoRamal.subCanal.id.eq(subCanalId));
        }

        return this;
    }

    public SolicitacaoRamalPredicate comEquipeId(Integer equipeId) {
        if (equipeId != null) {
            builder.and(solicitacaoRamal.equipeId.eq(equipeId));
        }

        return this;
    }
}
