package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Configuracao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfiguracaoResponseTest {

    @Test
    public void convertFrom_deveRetornarConfiguracaoResponse_quandoSolicitado() {
        assertThat(ConfiguracaoResponse.convertFrom(umaConfiguracao()))
            .extracting("id", "ramal", "usuarioId")
            .containsExactly(23, 33, 43);
    }

    private Configuracao umaConfiguracao() {
        var configuracao = new Configuracao();
        configuracao.setId(23);
        configuracao.setRamal(33);
        configuracao.setUsuario(new Usuario(43));
        return configuracao;
    }
}
