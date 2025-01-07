package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.ADMINISTRADOR;
import static org.assertj.core.api.Assertions.assertThat;

public class CargoRequestTest {

    @Test
    public void convertFrom_deveRetornarCargo_quandoSolicitado() {
        assertThat(CargoRequest.convertFrom(umCargoRequest()))
            .extracting("id", "nome", "nivel.id", "codigo", "situacao")
            .containsExactly(23, "nome", 34, ADMINISTRADOR, A);
    }

    @Test
    public void convertFrom_deveRetornarCargo_quandoCamposNullos() {
        assertThat(CargoRequest.convertFrom(umCargoRequestComCamposNulos()))
            .extracting("id", "nome", "nivel.id", "codigo", "situacao")
            .containsExactly(null, "nome", null, ADMINISTRADOR, null);
    }

    private CargoRequest umCargoRequest() {
        return CargoRequest.builder()
            .id(23)
            .nome("nome")
            .nivel(Nivel.builder()
                .id(34)
                .build())
            .codigo(ADMINISTRADOR)
            .situacao(A)
            .build();
    }

    private CargoRequest umCargoRequestComCamposNulos() {
        return CargoRequest.builder()
            .nome("nome")
            .codigo(ADMINISTRADOR)
            .build();
    }
}
