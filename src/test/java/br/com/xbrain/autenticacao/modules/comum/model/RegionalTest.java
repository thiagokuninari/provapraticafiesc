package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EAreaAtuacao;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegionalTest {

    @Test
    public void getTipo_deveRetornarRegional_quandoSolicitado() {
        assertThat(new Regional().getTipo())
            .isEqualTo(EAreaAtuacao.REGIONAL);
    }
}
