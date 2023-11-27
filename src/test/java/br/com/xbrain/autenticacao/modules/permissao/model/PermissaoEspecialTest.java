package br.com.xbrain.autenticacao.modules.permissao.model;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class PermissaoEspecialTest {

    @Test
    public void of_deveRetornarPermissaoEspecial_seUsuarioCadastroIdNaoNulo() {
        var permissaoEspecial = PermissaoEspecial.of(89, 4006, 32);

        assertThat(permissaoEspecial)
            .extracting("usuario.id", "funcionalidade.id", "usuarioCadastro.id")
            .containsExactly(89, 4006, 32);

        assertThat(permissaoEspecial.getDataCadastro())
            .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES));
    }

    @Test
    public void of_deveRetornarPermissaoEspecial_seUsuarioCadastroIdNulo() {
        var permissaoEspecial = PermissaoEspecial.of(89, 4006, null);

        assertThat(permissaoEspecial)
            .extracting("usuario.id", "funcionalidade.id", "usuarioCadastro.id")
            .containsExactly(89, 4006, null);

        assertThat(permissaoEspecial.getDataCadastro())
            .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES));
    }
}
