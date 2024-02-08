package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.model.QConfiguracaoAgenda.configuracaoAgenda;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfiguracaoAgendaPredicateTest {

    @Test
    public void comNivel_deveMontarPredicate_quandoPassarNivel() {
        assertThat(new ConfiguracaoAgendaPredicate().comNivel(CodigoNivel.AGENTE_AUTORIZADO).build())
            .isEqualTo(new BooleanBuilder(configuracaoAgenda.nivel.eq(CodigoNivel.AGENTE_AUTORIZADO)));
    }

    @Test
    public void comNivel_naoDeveMontarPredicate_quandoNaoPassarNivel() {
        assertThat(new ConfiguracaoAgendaPredicate().comNivel(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCanal_deveMontarPredicate_quandoPassarCanal() {
        assertThat(new ConfiguracaoAgendaPredicate().comCanal(ECanal.AGENTE_AUTORIZADO).build())
            .isEqualTo(new BooleanBuilder(configuracaoAgenda.canal.eq(ECanal.AGENTE_AUTORIZADO)));

    }

    @Test
    public void comCanal_naoDeveMontarPredicate_quandoNaoPassarCanal() {
        assertThat(new ConfiguracaoAgendaPredicate().comCanal(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comDescricao_deveMontarPredicate_quandoPassarDescricao() {
        assertThat(new ConfiguracaoAgendaPredicate().comDescricao("desc").build())
            .isEqualTo(new BooleanBuilder(configuracaoAgenda.descricao.containsIgnoreCase("desc")));

    }

    @Test
    public void comDescricao_naoDeveMontarPredicate_quandoNaoPassarDescricao() {
        assertThat(new ConfiguracaoAgendaPredicate().comDescricao(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comDescricao_naoDeveMontarPredicate_quandoDescricaoVazia() {
        assertThat(new ConfiguracaoAgendaPredicate().comDescricao(" ").build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comQtdHorasAdicionais_deveMontarPredicate_quandoPassarQtdHorasAdicionais() {
        assertThat(new ConfiguracaoAgendaPredicate().comQtdHorasAdicionais(10).build())
            .isEqualTo(new BooleanBuilder(configuracaoAgenda.qtdHorasAdicionais.eq(10)));
    }

    @Test
    public void comQtdHorasAdicionais_naoDeveMontarPredicate_quandoNaoPassarQtdHorasAdicionais() {
        assertThat(new ConfiguracaoAgendaPredicate().comQtdHorasAdicionais(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comSubCanal_deveMontarPredicate_quandoPassarSubCanal() {
        assertThat(new ConfiguracaoAgendaPredicate().comSubCanal(ETipoCanal.PAP).build())
            .isEqualTo(new BooleanBuilder(configuracaoAgenda.subcanal.eq(ETipoCanal.PAP)));
    }

    @Test
    public void comSubCanal_naoDeveMontarPredicate_quandoNaoPassarSubCanal() {
        assertThat(new ConfiguracaoAgendaPredicate().comSubCanal(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comEstruturaAa_deveMontarPredicate_quandoPassarEstruturaAa() {
        assertThat(new ConfiguracaoAgendaPredicate().comEstruturaAa("AGENTE_AUTORIZADO").build())
            .isEqualTo(new BooleanBuilder(configuracaoAgenda.estruturaAa.equalsIgnoreCase("AGENTE_AUTORIZADO")));
    }

    @Test
    public void comEstruturaAa_naoDeveMontarPredicate_quandoNaoPassarEstruturaAa() {
        assertThat(new ConfiguracaoAgendaPredicate().comEstruturaAa(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comEstruturaAa_naoDeveMontarPredicate_quandoEstruturaAaVazia() {
        assertThat(new ConfiguracaoAgendaPredicate().comEstruturaAa(" ").build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comSituacao_deveMontarPredicate_quandoPassarSituacao() {
        assertThat(new ConfiguracaoAgendaPredicate().comSituacao(ESituacao.A).build())
            .isEqualTo(new BooleanBuilder(configuracaoAgenda.situacao.eq(ESituacao.A)));
    }

    @Test
    public void comSituacao_naoDeveMontarPredicate_quandoNaoPassarSituacao() {
        assertThat(new ConfiguracaoAgendaPredicate().comSituacao(null).build())
            .isEqualTo(new BooleanBuilder());
    }
}
