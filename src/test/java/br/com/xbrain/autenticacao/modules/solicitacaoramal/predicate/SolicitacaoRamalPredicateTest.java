package br.com.xbrain.autenticacao.modules.solicitacaoramal.predicate;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.xbrainutils.DateUtils;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.model.QSolicitacaoRamal.solicitacaoRamal;
import static org.assertj.core.api.Assertions.assertThat;

public class SolicitacaoRamalPredicateTest {

    @Test
    public void comSituacaoSolicitacao_deveMontarPredicate_seHouverSituacao() {

        assertThat(new SolicitacaoRamalPredicate()
            .comSituacaoSolicitacao(ESituacaoSolicitacao.EM_ANDAMENTO)
            .build())
            .isEqualTo(new BooleanBuilder(solicitacaoRamal.situacao.eq(ESituacaoSolicitacao.EM_ANDAMENTO)));
    }

    @Test
    public void comDataCadastro_deveMontarPredicate_seHouverDatas() {
        var dataInicial = DateUtils.parseLocalDateToString(LocalDate.of(2022, 9, 28));
        var dataFinal = DateUtils.parseLocalDateToString(LocalDate.of(2022, 9, 28)
            .plusYears(3));

        assertThat(new SolicitacaoRamalPredicate()
            .comDataCadastro(dataInicial, dataFinal)
            .build()).isEqualTo(new BooleanBuilder(solicitacaoRamal.dataCadastro
            .between(DateUtils.parseStringToLocalDate(dataInicial).atStartOfDay(), DateUtils
                .parseStringToLocalDate(dataFinal).atTime(LocalTime.MAX))));
    }

    @Test
    public void comDataFinalizacaoNula_deveRetornarPredicateNulo_seDataFinalizacaoNula() {
        assertThat(new SolicitacaoRamalPredicate().comDataFinalizacaoNula().build())
            .isEqualTo(new BooleanBuilder(solicitacaoRamal.dataFinalizacao.isNull()));
    }

    @Test
    public void comAgenteAutorizadoId_deveRetornarPredicate_seHouverAgenteAutorizadoId() {
        assertThat(new SolicitacaoRamalPredicate().comAgenteAutorizadoId(1).build())
            .isEqualTo(new BooleanBuilder(solicitacaoRamal.agenteAutorizadoId.eq(1)));
    }

    @Test
    public void comCanal_deveMontarPredicate_seHouverCanal() {
        assertThat(new SolicitacaoRamalPredicate().comCanal(ECanal.D2D_PROPRIO).build())
            .isEqualTo(new BooleanBuilder(solicitacaoRamal.canal.eq(ECanal.D2D_PROPRIO)));
    }

    @Test
    public void comSubCanalId_deveRetornarPredicate_seHouverSubCanalId() {
        assertThat(new SolicitacaoRamalPredicate().comSubCanalId(1).build())
            .isEqualTo(new BooleanBuilder(solicitacaoRamal.subCanal.id.eq(1)));
    }

    @Test
    public void comEquipeId_deveMontarPredicate_seHouverEquipeId() {
        assertThat(new SolicitacaoRamalPredicate().comEquipeId(123).build())
            .isEqualTo(new BooleanBuilder(solicitacaoRamal.equipeId.eq(123)));
    }

    @Test
    public void comEquipeId_naoDeveMontarPredicate_seEquipeIdNulo() {
        assertThat(new SolicitacaoRamalPredicate().comEquipeId(null).build())
            .isEqualTo(new BooleanBuilder());
    }
}
