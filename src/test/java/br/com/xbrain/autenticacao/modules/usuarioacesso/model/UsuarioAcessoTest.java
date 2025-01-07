package br.com.xbrain.autenticacao.modules.usuarioacesso.model;

import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuario;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioAcessoTest {

    @Test
    public void of_deveRetornarUsuarioAcesso_quandoSolicitado() {
        assertThat(UsuarioAcesso.of(umUsuario()))
            .extracting("usuario.id", "usuario.nome")
            .containsExactly(100, "NED STARK");
    }

    @Test
    public void criaRegistroLogout_deveRetornarUsuarioAcesso_quandoSolicitado() {
        assertThat(UsuarioAcesso.criaRegistroLogout(2))
            .extracting("usuario.id", "flagLogout")
            .containsExactly(2, "V");
    }

    @Test
    public void criaRegistroAcesso_deveRetornarUsuarioAcesso_quandoSolicitado() {
        var usuarioAcesso = new UsuarioAcesso(1, "email@xbrain.com.br");
        assertThat(usuarioAcesso.criaRegistroAcesso(3))
            .extracting("usuario.id", "flagLogout")
            .containsExactly(3, "F");
    }
}
