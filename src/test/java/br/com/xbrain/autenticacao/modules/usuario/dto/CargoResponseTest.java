package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import org.junit.Test;

import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.AGENTE_AUTORIZADO;
import static org.assertj.core.api.Assertions.assertThat;

public class CargoResponseTest {

    @Test
    public void of_deveRetornarCargoResponse_quandoHouverSuperioresCadastrados() {
        assertThat(CargoResponse.of(umCargo()))
            .extracting("id", "nome", "codigo", "quantidadeSuperior", "possuiCargoSuperior", "situacao", "nivel", "canais")
            .containsExactly(23, "nome", "ADMINISTRADOR", 2, true, A, 4, Set.of(AGENTE_AUTORIZADO));
    }

    @Test
    public void of_deveRetornarCargoResponse_quandoNaoHouverSuperioresCadastrados() {
        var cargo = umCargo();
        cargo.setSuperiores(null);
        assertThat(CargoResponse.of(cargo))
            .extracting("id", "nome", "codigo", "quantidadeSuperior", "possuiCargoSuperior", "situacao", "nivel", "canais")
            .containsExactly(23, "nome", "ADMINISTRADOR", 2, false, A, 4, Set.of(AGENTE_AUTORIZADO));
    }

    private Cargo umCargo() {
        return Cargo.builder()
            .id(23)
            .nome("nome")
            .codigo(CodigoCargo.ADMINISTRADOR)
            .quantidadeSuperior(2)
            .superiores(Set.of(Cargo.builder().id(33).build()))
            .situacao(A)
            .nivel(Nivel.builder().id(4).build())
            .canais(Set.of(AGENTE_AUTORIZADO))
            .build();
    }
}
