package br.com.xbrain.autenticacao.modules.agenteautorizado.dto;

import br.com.xbrain.autenticacao.modules.usuario.dto.PublicoAlvoComunicadoFiltros;
import org.junit.Test;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelAa;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AgenteAutorizadoFiltrosTest {

    @Test
    public void of_deveRetornarAgenteAutorizadoFiltros_quandoSolicitado() {
        assertThat(AgenteAutorizadoFiltros.of(umPublicoAlvoComunicadoFiltros()))
            .extracting("cargoId", "departamentoId", "regionalId", "grupoId", "clusterId",
                "subClusterId", "ufId", "cidadesIds", "agentesAutorizadosIds")
            .containsExactly(null, null, 15, 14, 13, 16, 17, List.of(9, 10), List.of(1, 2));
    }

    private PublicoAlvoComunicadoFiltros umPublicoAlvoComunicadoFiltros() {
        return PublicoAlvoComunicadoFiltros.builder()
            .todoCanalD2d(false)
            .todoCanalAa(true)
            .agentesAutorizadosIds(List.of(1, 2))
            .equipesVendasIds(List.of(3, 4))
            .usuariosIds(List.of(5, 6))
            .cargosIds(List.of(7, 8))
            .cidadesIds(List.of(9, 10))
            .niveisIds(List.of(11, 12))
            .clusterId(13)
            .grupoId(14)
            .regionalId(15)
            .subClusterId(16)
            .ufId(17)
            .usuariosFiltradosPorCidadePol(List.of(18, 19))
            .novasRegionaisIds(List.of(20, 21))
            .usuarioAutenticado(umUsuarioAutenticadoNivelAa())
            .comUsuariosLogadosHoje(true)
            .build();
    }
}
