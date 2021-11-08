package br.com.xbrain.autenticacao.modules.usuario.predicate;

import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static org.assertj.core.api.Assertions.assertThat;

public class CidadePredicateTest {

    @Test
    public void comCodigoCidadeDbm_cidadePredicate_seCodigoCidadeDbmNula() {
        assertThat(new CidadePredicate().comCodigoCidadeDbm(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCodigoCidadeDbm_cidadePredicate_seCodigoCidadeDbmNaoNula() {
        assertThat(new CidadePredicate().comCodigoCidadeDbm(1).build())
            .isEqualTo(new BooleanBuilder(cidade.cidadesDbm.any().codigoCidadeDbm.eq(1)));
    }
}
