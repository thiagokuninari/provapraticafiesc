package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioResponseTest {

    @Test
    public void of_deveRetornarUsuarioAgenteAutorizadoResponse_seSolicitado() {
        assertThat(UsuarioResponse.of(umUsuario()))
            .extracting("id", "nome", "email", "aaId", "tipoCanal")
            .containsExactly(100, "Fulano de Teste", "teste@teste.com", 101, ETipoCanal.PAP_PREMIUM);
    }

    private static Usuario umUsuario() {
        return Usuario
            .builder()
            .id(100)
            .nome("Fulano de Teste")
            .email("teste@teste.com")
            .agenteAutorizadoId(101)
            .tipoCanal(ETipoCanal.PAP_PREMIUM)
            .build();
    }
}
