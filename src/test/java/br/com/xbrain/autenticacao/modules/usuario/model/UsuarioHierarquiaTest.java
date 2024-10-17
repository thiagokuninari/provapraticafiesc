package br.com.xbrain.autenticacao.modules.usuario.model;

import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioHierarquiaTest {

    @Test
    public void getUsuarioSuperiorId_deveRetornarUsuarioSuperiorId_quandoUsuarioSuperiorExistir() {
        var usuarioHierarquia = UsuarioHierarquia.builder()
            .usuarioSuperior(new Usuario(7890))
            .build();
        assertThat(usuarioHierarquia.getUsuarioSuperiorId()).isEqualTo(7890);
    }

    @Test
    public void getUsuarioSuperiorId_deveRetornaNull_quandoUsuarioSuperiorNaoExistir() {
        var usuarioHierarquia = UsuarioHierarquia.builder()
            .usuarioSuperior(null)
            .build();
        assertThat(usuarioHierarquia.getUsuarioSuperiorId()).isNull();
    }

    @Test
    public void isSuperior_deveRetornaTrue_seCargoSuperiorExistirECargosSuperioresIdConterNoParametro() {
        var usuarioHierarquia = UsuarioHierarquia.builder()
            .usuario(Usuario.builder()
                .cargo(Cargo.builder()
                    .superiores(Set.of(Cargo.builder().id(89).build()))
                    .build())
                .build())
            .build();
        assertThat(usuarioHierarquia.isSuperior(89)).isTrue();
    }

    @Test
    public void isSuperior_deveRetornaFalse_seCargoSuperiorExistirECargosSuperioresIdNaoConterNoParametro() {
        var usuarioHierarquia = UsuarioHierarquia.builder()
            .usuario(Usuario.builder().build())
            .build();

        assertThat(usuarioHierarquia.isSuperior(89)).isFalse();
    }
}
