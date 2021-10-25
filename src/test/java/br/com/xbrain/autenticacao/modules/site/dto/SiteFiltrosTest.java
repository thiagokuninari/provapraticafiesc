package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SiteFiltrosTest {

    @Test
    public void toPredicate_deveRetornarSitePredicate_seSolicitado() {
        var atual = SiteFiltros
            .builder()
            .id(1)
            .nome("NOME 1")
            .timeZone(ETimeZone.BRT)
            .situacao(ESituacao.A)
            .coordenadoresIds(List.of(1, 2, 3))
            .supervisoresIds(List.of(1, 2, 3))
            .estadosIds(List.of(1, 2, 3))
            .cidadesIds(List.of(1, 2, 3))
            .naoPossuiDiscadora(Boolean.TRUE)
            .discadoraId(1)
            .ids(List.of(1, 2, 3))
            .build()
            .toPredicate()
            .build();
        var esperado = new SitePredicate()
            .comId(1)
            .comNome("NOME 1")
            .comTimeZone(ETimeZone.BRT)
            .comSituacao(ESituacao.A)
            .naoPossuiDiscadora(Boolean.TRUE)
            .comDiscadoraId(1)
            .comCoordenadores(List.of(1, 2, 3))
            .comSupervisores(List.of(1, 2, 3))
            .comEstados(List.of(1, 2, 3))
            .comCidades(List.of(1, 2, 3))
            .comIds(List.of(1, 2, 3))
            .build();

        assertThat(atual)
            .isEqualTo(esperado);
    }
}
