package br.com.xbrain.autenticacao.modules.solicitacaoramal.predicate;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.model.QSolicitacaoRamal.solicitacaoRamal;
import static org.assertj.core.api.Assertions.assertThat;

public class SolicitacaoRamalPredicateTest {

    @Test
    public void comSituacaoSolicitacao_deveMontarPredicate_quandoInformarSituacao() {
        assertThat(new SolicitacaoRamalPredicate().comSituacaoSolicitacao(ESituacaoSolicitacao.ENVIADO).build())
            .isEqualTo(new BooleanBuilder(solicitacaoRamal.situacao.eq(ESituacaoSolicitacao.ENVIADO)));
    }

    @Test
    public void comSituacaoSolicitacao_naoDeveMontarPredicate_quandoNaoInformarSituacao() {
        assertThat(new SolicitacaoRamalPredicate().comSituacaoSolicitacao(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comDataCadastro_deveMontarPredicate_quandoInformarData() {
        assertThat(new SolicitacaoRamalPredicate().comDataCadastro("10/05/2023", "15/05/2023").build())
            .isEqualTo(new BooleanBuilder(solicitacaoRamal.dataCadastro.between(
                LocalDate.of(2023, 5, 10).atTime(LocalTime.MIN),
                LocalDate.of(2023, 5, 15).atTime(LocalTime.MAX))));
    }

    @Test
    public void comDataCadastro_naoDeveMontarPredicate_quandoInformarDataNull() {
        assertThat(new SolicitacaoRamalPredicate().comDataCadastro(null, null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comDataFinalizacaoNula_deveMontarPredicate_quandoDataFinalizacaoNull() {
        assertThat(new SolicitacaoRamalPredicate().comDataFinalizacaoNula().build())
            .isEqualTo(new BooleanBuilder(solicitacaoRamal.dataFinalizacao.isNull()));
    }

    @Test
    public void comAgenteAutorizadoId_deveMontarPredicate_quandoinformarAgenteAutorizadoId() {
        assertThat(new SolicitacaoRamalPredicate().comAgenteAutorizadoId(1).build())
            .isEqualTo(new BooleanBuilder(solicitacaoRamal.agenteAutorizadoId.eq(1)));
    }

    @Test
    public void comAgenteAutorizadoId_naoDeveMontarPredicate_quandoNaoinformarAgenteAutorizadoId() {
        assertThat(new SolicitacaoRamalPredicate().comAgenteAutorizadoId(null).build())
            .isEqualTo(new BooleanBuilder());
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
