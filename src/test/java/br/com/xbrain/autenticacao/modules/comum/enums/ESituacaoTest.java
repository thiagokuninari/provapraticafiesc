package br.com.xbrain.autenticacao.modules.comum.enums;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ESituacaoTest {

    @Test
    public void getOnlyAtivoInativo_deveRetornarSomenteAtivoAndInativo_quandoOk() {
        assertThat(ESituacao.getOnlyAtivoInativo())
            .containsExactlyInAnyOrder(ESituacao.A, ESituacao.I);
    }
}
