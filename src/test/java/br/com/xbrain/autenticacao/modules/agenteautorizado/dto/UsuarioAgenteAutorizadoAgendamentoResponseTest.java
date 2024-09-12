package br.com.xbrain.autenticacao.modules.agenteautorizado.dto;

import org.junit.Test;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class UsuarioAgenteAutorizadoAgendamentoResponseTest {

    @Test
    public void of_deveRetornarUsuarioAgenteAutorizadoAgendamentoResponse_seSolicitado() {
        assertThat(UsuarioAgenteAutorizadoAgendamentoResponse.of(umUsuario()))
            .extracting("id", "nome", "equipeVendasId", "equipeVendasNome", "supervisorNome", "cargoUsuario")
            .containsExactly(100, "NED STARK", null, null, null, "ADMINISTRADOR");
    }

    @Test
    public void of_deveRetornarListaUsuarioAgenteAutorizadoAgendamentoResponse_seSolicitado() {
        assertThat(UsuarioAgenteAutorizadoAgendamentoResponse.of(List.of(umUsuario())))
            .extracting("id", "nome", "equipeVendasId", "equipeVendasNome", "supervisorNome", "cargoUsuario")
            .containsExactly(tuple(100, "NED STARK", null, null, null, "ADMINISTRADOR"));
    }

    @Test
    public void isUsuarioSolicitante_deveRetornarTrue_quandoUsuarioSolicitanteIdIgualAoId() {
        assertThat(umUsuarioAgenteAutorizadoAgendamentoResponse().isUsuarioSolicitante(1))
            .isTrue();
    }

    @Test
    public void isUsuarioSolicitante_deveRetornarFalse_quandoUsuarioSolicitanteIdIgualAoId() {
        assertThat(umUsuarioAgenteAutorizadoAgendamentoResponse().isUsuarioSolicitante(2))
            .isFalse();
    }

    private UsuarioAgenteAutorizadoAgendamentoResponse umUsuarioAgenteAutorizadoAgendamentoResponse() {
        return UsuarioAgenteAutorizadoAgendamentoResponse.builder()
            .id(1)
            .nome("nome")
            .build();
    }
}
