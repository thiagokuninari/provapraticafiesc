package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.I;
import static br.com.xbrain.autenticacao.modules.comum.enums.Eboolean.V;
import static br.com.xbrain.autenticacao.modules.usuario.model.QSubCanal.subCanal;
import static org.assertj.core.api.Assertions.assertThat;

public class SubCanalPredicateTest {

    @Test
    public void comCodigo_deveFiltrarPorCodigo_quandoCodigoListaPresenteNoFiltro() {
        var tiposCanais = List.of(ETipoCanal.PAP_PME, ETipoCanal.INSIDE_SALES_PME);
        var predicate = new SubCanalPredicate()
            .comCodigo(tiposCanais)
            .build();

        var expected = new BooleanBuilder(subCanal.codigo.in(tiposCanais));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comNome_deveFiltrarPorNome_quandoNomePresenteNoFiltro() {
        var predicate = new SubCanalPredicate()
            .comNome("Um SubCanal Nome")
            .build();

        var expected = new BooleanBuilder(subCanal.nome.equalsIgnoreCase("Um SubCanal Nome"));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comSituacao_deveFiltrarPorSituacao_quandoSituacaoListaPresenteNoFiltro() {
        var situacoes = List.of(A, I);
        var predicate = new SubCanalPredicate()
            .comSituacao(situacoes)
            .build();

        var expected = new BooleanBuilder(subCanal.situacao.in(situacoes));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comNovaChecagemCredito_deveFiltrarPorNovaChecagemCredito_quandoNovaChecagemPresenteNoFiltro() {
        var predicate = new SubCanalPredicate()
            .comNovaChecagemCredito(V)
            .build();

        var expected = new BooleanBuilder(subCanal.novaChecagemCredito.eq(V));
        assertThat(predicate).isEqualTo(expected);
    }
}
