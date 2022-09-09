package br.com.xbrain.autenticacao.modules.usuario.predicate;

import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.model.QCidadeDbm.cidadeDbm;
import static org.assertj.core.api.Assertions.assertThat;

public class CidadeDbmPredicateTest {

    @Test
    public void comCodigoCidadeDbm_cidadePredicate_seCodigoCidadeDbmNula() {
        assertThat(new CidadeDbmPredicate().comCodigoCidadeDbm(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCodigoCidadeDbm_cidadePredicate_seCodigoCidadeDbmNaoNula() {
        assertThat(new CidadeDbmPredicate().comCodigoCidadeDbm(1).build())
            .isEqualTo(new BooleanBuilder(cidadeDbm.codigoCidadeDbm.eq(1)));
    }

    @Test
    public void comDdd_cidadePredicate_seDddNull() {
        assertThat(new CidadeDbmPredicate().comDdd(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comDdd_cidadePredicate_seDddNaoNull() {
        assertThat(new CidadeDbmPredicate().comDdd(1).build())
            .isEqualTo(new BooleanBuilder(cidadeDbm.ddd.eq(1)));
    }
}
