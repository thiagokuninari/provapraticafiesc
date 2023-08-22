package br.com.xbrain.autenticacao.modules.feriado.predicate;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.QImportacaoFeriado.importacaoFeriado;
import static br.com.xbrain.autenticacao.modules.feriado.model.QFeriado.feriado;


public class FeriadoPredicateTest {

    @Test
    public void comSituacaoFeriadoAutomacao_deveMontarPredicate_seSituacaoFeriadoImportacaoNaoNulo() {
        assertThat(new FeriadoPredicate().comSituacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO).build())
            .isEqualTo(new BooleanBuilder(importacaoFeriado.situacaoFeriadoAutomacao.eq(ESituacaoFeriadoAutomacao.IMPORTADO)));
    }

    @Test
    public void comSituacaoFeriadoAutomacao_deveMontarPredicate_seSituacaoFeriadoImportacaoNulo() {
        assertThat(new FeriadoPredicate().comSituacaoFeriadoAutomacao(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCidade_deveMontarPredicate_deCidadeIdNaoNulo() {
        assertThat(new FeriadoPredicate().comCidadeId(1).build())
            .isEqualTo(new BooleanBuilder(feriado.cidade.id.eq(1).or(feriado.feriadoNacional.eq(Eboolean.V))));
    }

    @Test
    public void comCidade_deveMontarPredicate_deCidadeIdNulo() {
        assertThat(new FeriadoPredicate().comCidadeId(null).build())
            .isEqualTo(new BooleanBuilder());
    }

}
