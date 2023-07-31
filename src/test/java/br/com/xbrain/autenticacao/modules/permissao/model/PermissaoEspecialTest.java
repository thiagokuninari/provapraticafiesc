package br.com.xbrain.autenticacao.modules.permissao.model;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class PermissaoEspecialTest {

    @Test
    public void of_deveRetornarPermissaoEspecial_quandoChamado() {
        var permissao = PermissaoEspecial.of(1, 2, 3);
        assertThat(permissao.getUsuario().getId()).isEqualTo(1);
        assertThat(permissao.getFuncionalidade().getId()).isEqualTo(2);
        assertThat(permissao.getUsuarioCadastro().getId()).isEqualTo(3);
        assertThat(permissao.getDataCadastro())
            .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES));
    }
}