package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.model.QConfiguracaoAgendaReal.configuracaoAgendaReal;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfiguracaoAgendaRealPredicateTest {

    @Test
    public void comNivel_deveMontarPredicate_quandoPassarNivel() {
        assertThat(new ConfiguracaoAgendaRealPredicate().comNivel(CodigoNivel.AGENTE_AUTORIZADO).build())
            .isEqualTo(new BooleanBuilder(configuracaoAgendaReal.nivel.eq(CodigoNivel.AGENTE_AUTORIZADO)));
    }

    @Test
    public void comNivel_naoDeveMontarPredicate_quandoNaoPassarNivel() {
        assertThat(new ConfiguracaoAgendaRealPredicate().comNivel(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCanal_deveMontarPredicate_quandoPassarCanal() {
        assertThat(new ConfiguracaoAgendaRealPredicate().comCanal(ECanal.AGENTE_AUTORIZADO).build())
            .isEqualTo(new BooleanBuilder(configuracaoAgendaReal.canal.eq(ECanal.AGENTE_AUTORIZADO)));

    }

    @Test
    public void comCanal_naoDeveMontarPredicate_quandoNaoPassarCanal() {
        assertThat(new ConfiguracaoAgendaRealPredicate().comCanal(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comQtdHorasAdicionais_deveMontarPredicate_quandoPassarQtdHorasAdicionais() {
        assertThat(new ConfiguracaoAgendaRealPredicate().comQtdHorasAdicionais(10).build())
            .isEqualTo(new BooleanBuilder(configuracaoAgendaReal.qtdHorasAdicionais.eq(10)));
    }

    @Test
    public void comQtdHorasAdicionais_naoDeveMontarPredicate_quandoNaoPassarQtdHorasAdicionais() {
        assertThat(new ConfiguracaoAgendaRealPredicate().comQtdHorasAdicionais(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comSubCanal_deveMontarPredicate_quandoPassarSubCanal() {
        assertThat(new ConfiguracaoAgendaRealPredicate().comSubCanal(ETipoCanal.PAP.getId()).build())
            .isEqualTo(new BooleanBuilder(configuracaoAgendaReal.subcanalId.eq(ETipoCanal.PAP.getId())));
    }

    @Test
    public void comSubCanal_naoDeveMontarPredicate_quandoNaoPassarSubCanal() {
        assertThat(new ConfiguracaoAgendaRealPredicate().comSubCanal(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comEstruturaAa_deveMontarPredicate_quandoPassarEstruturaAa() {
        assertThat(new ConfiguracaoAgendaRealPredicate().comEstruturaAa("AGENTE_AUTORIZADO").build())
            .isEqualTo(new BooleanBuilder(configuracaoAgendaReal.estruturaAa.equalsIgnoreCase("AGENTE_AUTORIZADO")));
    }

    @Test
    public void comEstruturaAa_naoDeveMontarPredicate_quandoNaoPassarEstruturaAa() {
        assertThat(new ConfiguracaoAgendaRealPredicate().comEstruturaAa(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comEstruturaAa_naoDeveMontarPredicate_quandoEstruturaAaVazia() {
        assertThat(new ConfiguracaoAgendaRealPredicate().comEstruturaAa(" ").build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comSituacao_deveMontarPredicate_quandoPassarSituacao() {
        assertThat(new ConfiguracaoAgendaRealPredicate().comSituacao(ESituacao.A).build())
            .isEqualTo(new BooleanBuilder(configuracaoAgendaReal.situacao.eq(ESituacao.A)));
    }

    @Test
    public void comSituacao_naoDeveMontarPredicate_quandoNaoPassarSituacao() {
        assertThat(new ConfiguracaoAgendaRealPredicate().comSituacao(null).build())
            .isEqualTo(new BooleanBuilder());
    }
}
