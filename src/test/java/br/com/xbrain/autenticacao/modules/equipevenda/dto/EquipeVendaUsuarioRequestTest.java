package br.com.xbrain.autenticacao.modules.equipevenda.dto;

import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuario;
import static org.assertj.core.api.Assertions.assertThat;

public class EquipeVendaUsuarioRequestTest {

    @Test
    public void of_deveGerarRequest_quandoChamado() {
        assertThat(EquipeVendaUsuarioRequest.of(umUsuario()))
            .extracting("usuarioId", "usuarioNome")
            .containsExactly(100, "NED STARK");
    }
}
