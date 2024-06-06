package br.com.xbrain.autenticacao.modules.call.dto;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ConfiguracaoTelefoniaResponseTest {

    @Test
    public void getText_deveConcatenarIpENome_quandoSolicitado() {
        var configuracaoTelefoniaResponse = ConfiguracaoTelefoniaResponse.builder()
            .ip("192.168.0.1")
            .nome("Nome")
            .build();

        assertThat(configuracaoTelefoniaResponse.getText())
            .isEqualTo("192.168.0.1 - Nome");
    }
}
