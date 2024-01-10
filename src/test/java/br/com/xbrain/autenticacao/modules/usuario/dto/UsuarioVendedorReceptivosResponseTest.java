package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioVendedorReceptivosResponseTest {

    @Test
    public void of_deveRetornarUsuarioVendedorReceptivoResponse_quandoValido() {
        var usuario = umUsuario();
        var usuariosVendedorReceptivoResponse = UsuarioVendedorReceptivoResponse.of(usuario);
        assertThat(usuariosVendedorReceptivoResponse).isInstanceOf(UsuarioVendedorReceptivoResponse.class);
        assertThat(usuariosVendedorReceptivoResponse)
            .extracting("nome", "email", "loginNetSales", "nivel", "organizacao")
            .containsExactly(usuario.getNome(), usuario.getEmail(), usuario.getLoginNetSales(),
                usuario.getNivelNome(), usuario.getOrganizacaoEmpresa().getDescricao());
    }

    private Usuario umUsuario() {
        return Usuario.builder()
            .id(1)
            .nome("Teste")
            .email("testa@gmail.com")
            .loginNetSales("loginteste")
            .cargo(Cargo.builder()
                .id(1)
                .nivel(Nivel.builder()
                    .id(1)
                    .nome("Nivel teste")
                    .build())
                .build())
            .organizacaoEmpresa(OrganizacaoEmpresa.builder().codigo("1").nome("Org teste").build())
            .build();
    }
}
