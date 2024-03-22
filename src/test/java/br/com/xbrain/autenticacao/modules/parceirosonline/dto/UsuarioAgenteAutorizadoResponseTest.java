package br.com.xbrain.autenticacao.modules.parceirosonline.dto;

import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioAgenteAutorizadoResponseTest {
    @Test
    public void of_deveRetornarUsuarioAgenteAutorizadoResponse_seSolicitado() {
        assertThat(UsuarioAgenteAutorizadoResponse.of(umUsuario()))
            .extracting("id", "nome", "email", "agenteAutorizadoId")
            .containsExactly(100, "Fulano de Teste", "teste@teste.com", 101);
    }

    private static Usuario umUsuario() {
        return Usuario
            .builder()
            .id(100)
            .nome("Fulano de Teste")
            .email("teste@teste.com")
            .agenteAutorizadoId(101)
            .build();
    }
}
