package br.com.xbrain.autenticacao.modules.feeder.enums;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ECargosComPermissaoAaFeederTest {

    @Test
    public void listaDeCargos_deveListarTodosCodigosDosCargos_quandoChamado() {
        var valor = List.of(41,42,56,78,84,88,47,45);

        assertThat(ECargosComPermissaoAaFeeder.listaDeCargos()).isEqualTo(valor);
    }
}
