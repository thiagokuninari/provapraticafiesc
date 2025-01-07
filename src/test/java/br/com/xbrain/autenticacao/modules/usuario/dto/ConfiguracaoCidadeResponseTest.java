package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfiguracaoCidadeResponseTest {

    @Test
    public void of_deveRetornarConfiguracaoCidadeResponse_quandoSolicitado() {
        assertThat(ConfiguracaoCidadeResponse.of(CidadeHelper.cidadeLondrina()))
            .extracting(ConfiguracaoCidadeResponse::getId, ConfiguracaoCidadeResponse::getNome, ConfiguracaoCidadeResponse::getUf)
            .containsExactly(5578, "LONDRINA", "PR");
    }
}
