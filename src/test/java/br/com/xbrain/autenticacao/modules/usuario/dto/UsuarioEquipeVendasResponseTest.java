package br.com.xbrain.autenticacao.modules.usuario.dto;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioEquipeVendasResponseTest {

    @Test
    public void convertFrom_deveRetornarUsuarioEquipeVendasResponse_quandoSolicitad() {
        assertThat(UsuarioEquipeVendasResponse.convertFrom(umUsuarioResponse()))
            .extracting("id", "usuarioId", "usuarioNome", "cargoNome")
            .containsExactly(null, 23, "nome do cara", null);
    }

    private UsuarioResponse umUsuarioResponse() {
        return UsuarioResponse.builder()
            .id(23)
            .nome("nome do cara")
            .build();
    }
}
