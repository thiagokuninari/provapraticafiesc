package br.com.xbrain.autenticacao.modules.site.predicate;

import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.site.model.QSite.site;
import static org.assertj.core.api.Assertions.assertThat;

public class SitePredicateTest {

    @Test
    public void comCidade_sitePredicate_seCidadeNula() {
        assertThat(new SitePredicate().comCidade(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCidade_sitePredicate_seCidadeVazia() {
        assertThat(new SitePredicate().comCidade("").build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCidade_sitePredicate_seCidadeNaoNulaENaoVazia() {
        assertThat(new SitePredicate().comCidade("CIDADE").build())
            .isEqualTo(new BooleanBuilder(site.cidades.any().nome.eq("CIDADE")));
    }

    @Test
    public void comUf_sitePredicate_seUfNula() {
        assertThat(new SitePredicate().comUf(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comUf_sitePredicate_seUfVazia() {
        assertThat(new SitePredicate().comUf("").build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comUf_sitePredicate_seUfNaoNulaENaoVazia() {
        assertThat(new SitePredicate().comUf("UF").build())
            .isEqualTo(new BooleanBuilder(site.cidades.any().uf.uf.eq("UF")));
    }

    @Test
    public void comCodigoCidadeDbm_sitePredicate_seCodigoCidadeDbmNula() {
        assertThat(new SitePredicate().comCodigoCidadeDbm(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCodigoCidadeDbm_sitePredicate_seCodigoCidadeDbmNaoNula() {
        assertThat(new SitePredicate().comCodigoCidadeDbm(1).build())
            .isEqualTo(new BooleanBuilder(site.cidades.any().codigoCidadeDbm.eq(1)));
    }
}
