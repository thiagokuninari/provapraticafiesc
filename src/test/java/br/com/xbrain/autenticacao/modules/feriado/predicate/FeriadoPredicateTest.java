package br.com.xbrain.autenticacao.modules.feriado.predicate;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import java.time.LocalDate;

import static br.com.xbrain.autenticacao.modules.feriado.model.QFeriado.feriado;
import static org.assertj.core.api.Assertions.assertThat;
import static br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.QImportacaoFeriado.importacaoFeriado;

public class FeriadoPredicateTest {

    @Test
    public void comNome_deveMontarPredicate_quandoInformarNome() {
        assertThat(new FeriadoPredicate().comNome("Teste").build())
            .isEqualTo(new BooleanBuilder(feriado.nome.containsIgnoreCase("Teste")));
    }

    @Test
    public void comNome_deveMontarPredicate_quandoInformarNomeBlank() {
        assertThat(new FeriadoPredicate().comNome("  ").build())
            .isEqualTo(new BooleanBuilder(feriado.nome.containsIgnoreCase("  ")));
    }

    @Test
    public void comNome_naoDeveMontarPredicate_quandoInformarNomeNull() {
        assertThat(new FeriadoPredicate().comNome(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comTipoFeriado_deveMontarPredicate_quandoInformarTipoFeriado() {
        assertThat(new FeriadoPredicate().comTipoFeriado(ETipoFeriado.ESTADUAL).build())
            .isEqualTo(new BooleanBuilder(feriado.tipoFeriado.eq(ETipoFeriado.ESTADUAL)));
    }

    @Test
    public void comTipoFeriado_naoDeveMontarPredicate_quandoNaoInformarTipoFeriado() {
        assertThat(new FeriadoPredicate().comTipoFeriado(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comPeriodoDeDataFeriado_deveMontarPredicate_quandoInformarDataFeriado() {
        assertThat(new FeriadoPredicate().comPeriodoDeDataFeriado(LocalDate.of(2023, 5, 6),
            LocalDate.of(2023, 5, 9)).build())
            .isEqualTo(new BooleanBuilder(feriado.dataFeriado.between(LocalDate.of(2023, 5, 6),
                LocalDate.of(2023, 5, 9))));
    }

    @Test
    public void comPeriodoDeDataFeriado_naoDeveMontarPredicate_quandoNaoInformarDataFeriado() {
        assertThat(new FeriadoPredicate().comPeriodoDeDataFeriado(null, null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comPeriodoDeDataFeriado_naoDeveMontarPredicate_quandoNaoInformarDataFim() {
        assertThat(new FeriadoPredicate()
            .comPeriodoDeDataFeriado(LocalDate.of(2023, 12, 12), null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCidadeOuEstado_deveMontarPredicateComEstado_quandoCidadeIdForNull() {
        assertThat(new FeriadoPredicate().comCidadeOuEstado(null, 1).build())
            .isEqualTo(new BooleanBuilder(
                feriado.uf.id.eq(1)
                    .or(feriado.feriadoNacional.eq(Eboolean.V))));
    }

    @Test
    public void comCidadeOuEstado_deveMontarPredicateComCidade_quandoInformarCidadeIdEEstadoId() {
        assertThat(new FeriadoPredicate().comCidadeOuEstado(1, 2).build())
            .isEqualTo(new BooleanBuilder(
                feriado.cidade.id.eq(1)
                    .or(feriado.feriadoNacional.eq(Eboolean.V))
                    .or(feriado.uf.id.eq(2)
                        .and(feriado.tipoFeriado.eq(ETipoFeriado.ESTADUAL)))));
    }

    @Test
    public void comCidadeOuEstado_naoDeveMontarPredicateComCidade_quandoCidadeIdEEstadoIdForNull() {
        assertThat(new FeriadoPredicate().comCidadeOuEstado(null, null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCidade_deveMontarPredicate_quandoInformarCidadeIdEEstadoId() {
        assertThat(new FeriadoPredicate().comCidade(1, 2).build())
            .isEqualTo(new BooleanBuilder(
                feriado.cidade.id.eq(1)
                    .or(feriado.feriadoNacional.eq(Eboolean.V))
                    .or(feriado.uf.id.eq(2)
                        .and(feriado.tipoFeriado.eq(ETipoFeriado.ESTADUAL)))));
    }

    @Test
    public void comCidade_naoDeveMontarPredicate_quandoNaoInformarCidadeIdEEstadoId() {
        assertThat(new FeriadoPredicate().comCidade(null, null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCidade_naoDeveMontarPredicate_quandoNaoInformarEstadoId() {
        assertThat(new FeriadoPredicate().comCidade(1, null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comEstado_deveMontarPredicate_quandoInformarEstadoId() {
        assertThat(new FeriadoPredicate().comEstado(2).build())
            .isEqualTo(new BooleanBuilder(
                feriado.uf.id.eq(2)
                    .or(feriado.feriadoNacional.eq(Eboolean.V))));
    }

    @Test
    public void comEstado_naoDeveMontarPredicate_quandoNaoInformarEstadoId() {
        assertThat(new FeriadoPredicate().comEstado(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comDataFeriado_deveMontarPredicate_quandoInformarDataFeriado() {
        assertThat(new FeriadoPredicate().comDataFeriado(LocalDate.of(2023, 5, 9)).build())
            .isEqualTo(new BooleanBuilder(feriado.dataFeriado.eq(LocalDate.of(2023, 5, 9))));
    }

    @Test
    public void comDataFeriado_naoDeveMontarPredicate_quandoNaoInformarDataFeriado() {
        assertThat(new FeriadoPredicate().comDataFeriado(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void excetoFeriadosFilhos_deveMontarPredicate_quandoNaoPassarNada() {
        assertThat(new FeriadoPredicate().excetoFeriadosFilhos().build())
            .isEqualTo(new BooleanBuilder(feriado.feriadoPai.isNull()));
    }

    @Test
    public void comFeriadoPaiId_deveMontarPredicate_quandoInformarFeriadoPaiId() {
        assertThat(new FeriadoPredicate().comFeriadoPaiId(1).build())
            .isEqualTo(new BooleanBuilder(feriado.feriadoPai.id.eq(1)));
    }

    @Test
    public void comFeriadoPaiId_naoDeveMontarPredicate_quandoNaoInformarFeriadoPaiId() {
        assertThat(new FeriadoPredicate().comFeriadoPaiId(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void excetoExcluidos_deveMontarPredicate_quandoNaoPassarNada() {
        assertThat(new FeriadoPredicate().excetoExcluidos().build())
            .isEqualTo(new BooleanBuilder(feriado.situacao.ne(ESituacaoFeriado.EXCLUIDO)));
    }

    @Test
    public void comSituacaoFeriadoAutomacao_deveMontarPredicate_quandoSituacaoFeriadoAutomacaoNaoForNulo() {
        assertThat(new FeriadoPredicate().comSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.EM_IMPORTACAO).build())
            .isEqualTo(new BooleanBuilder(importacaoFeriado.situacaoFeriadoAutomacao
                .eq(ESituacaoFeriadoAutomacao.EM_IMPORTACAO)));
    }

    @Test
    public void comSituacaoFeriadoAutomacao_naoDeveMontarPredicate_quandoSituacaoFeriadoAutomacaoForNulo() {
        assertThat(new FeriadoPredicate().comSituacaoFeriadoAutomacao(null).build())
            .isEqualTo(new BooleanBuilder());
    }
}
