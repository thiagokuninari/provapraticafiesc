package br.com.xbrain.autenticacao.modules.agenteautorizado.dto;

import org.junit.Test;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.agenteautorizado.helper.UsuarioDtoVendasHelper.umPublicoAlvoComunicadoFiltros;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AgenteAutorizadoFiltrosTest {

    @Test
    public void of_deveRetornarAgenteAutorizadoFiltros_quandoSolicitado() {
        assertThat(AgenteAutorizadoFiltros.of(umPublicoAlvoComunicadoFiltros()))
            .extracting("cargoId", "departamentoId", "regionalId", "ufId", "cidadesIds",
                "agentesAutorizadosIds")
            .containsExactly(null, null, 15, 17, List.of(9, 10), List.of(1, 2));
    }
}
