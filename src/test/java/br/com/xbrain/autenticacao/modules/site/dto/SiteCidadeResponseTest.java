package br.com.xbrain.autenticacao.modules.site.dto;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SiteCidadeResponseTest {

    @Test
    public void construtor_deveRetornarSiteCidadeResponse_seSolicitado() {
        var atual = new SiteCidadeResponse(1, "SITE NOME", 1, "CIDADE NOME", 1, "UF NOME");
        var esperado = SiteCidadeResponse
            .builder()
            .siteId(1)
            .siteNome("SITE NOME")
            .cidadeId(1)
            .cidadeNome("CIDADE NOME")
            .ufId(1)
            .ufNome("UF NOME")
            .build();

        assertThat(atual)
            .isEqualToComparingFieldByField(esperado);
    }
}
