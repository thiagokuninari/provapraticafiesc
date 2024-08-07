package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SolicitacaoRamalColaboradorResponseTest {

    @Test
    public void convertFrom_deveRetornarSolicitacaoRamalColaboradorResponse_quandoSolicitado() {
        assertThat(SolicitacaoRamalColaboradorResponse.convertFrom(umUsuario()))
            .extracting("id", "nome", "cargo")
            .containsExactly(2, "nome", "nome do cargo");
    }

    @Test
    public void convertFrom_deveRetornarSolicitacaoRamalColaboradorResponseComCargoVazio_quandoCargoUsuarioNull() {
        var usuario = umUsuario();
        usuario.setCargo(null);
        assertThat(SolicitacaoRamalColaboradorResponse.convertFrom(usuario))
            .extracting("id", "nome", "cargo")
            .containsExactly(2, "nome", "");
    }

    private Usuario umUsuario() {
        return Usuario.builder()
            .id(2)
            .nome("nome")
            .cargo(Cargo.builder()
                .nome("nome do cargo")
                .build())
            .build();
    }
}
