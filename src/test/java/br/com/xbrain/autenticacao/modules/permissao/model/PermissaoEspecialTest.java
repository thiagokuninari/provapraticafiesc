package br.com.xbrain.autenticacao.modules.permissao.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PermissaoEspecialTest {

    @Test
    public void of_deveRetornarPermissaoEspecial_quandoSolicitado() {
        assertThat(PermissaoEspecial.of(89, 32, 4006))
            .extracting("usuario.id", "usuarioCadastro.id", "funcionalidade.id")
            .containsExactly(89, 32, 4006);
    }
}
