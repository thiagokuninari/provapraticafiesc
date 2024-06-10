package br.com.xbrain.autenticacao.modules.importacao.dto;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioImportacaoTest {

    @Test
    public void trimProperties_deveRemoverEspacosEmBranco_quandoSolicitado() {
        var usuario = umUsuarioImportacao();

        usuario.setNome("nome   ");
        usuario.setEmail("usuarioimportacao@xbrain.com.br   ");
        usuario.setEmail02("usuarioimportacao2@xbrain.com.br   ");
        usuario.setEmail03("usuarioimportacao3@xbrain.com.br   ");
        assertThat(usuario)
            .extracting("nome", "email", "email02", "email03")
            .containsExactly("nome   ",
                "usuarioimportacao@xbrain.com.br   ",
                "usuarioimportacao2@xbrain.com.br   ",
                "usuarioimportacao3@xbrain.com.br   ");

        usuario.trimProperties();

        assertThat(usuario)
            .extracting("nome", "email", "email02", "email03")
            .containsExactly("nome",
                "usuarioimportacao@xbrain.com.br",
                "usuarioimportacao2@xbrain.com.br",
                "usuarioimportacao3@xbrain.com.br");
    }

    @Test
    public void toUpperCaseProperties_deveRetornarCamposUpperCase_quandoSolicitado() {
        var usuario = umUsuarioImportacao();

        assertThat(usuario)
            .extracting("nome", "email", "email02", "email03")
            .containsExactly("nome",
                "usuarioimportacao@xbrain.com.br",
                "usuarioimportacao2@xbrain.com.br",
                "usuarioimportacao3@xbrain.com.br");

        usuario.toUpperCaseProperties();

        assertThat(usuario)
            .extracting("nome", "email", "email02", "email03")
            .containsExactly("NOME",
                "USUARIOIMPORTACAO@XBRAIN.COM.BR",
                "USUARIOIMPORTACAO2@XBRAIN.COM.BR",
                "USUARIOIMPORTACAO3@XBRAIN.COM.BR");
    }

    private UsuarioImportacao umUsuarioImportacao() {
        var usuarioImportacao = new UsuarioImportacao();
        usuarioImportacao.setNome("nome");
        usuarioImportacao.setEmail("usuarioimportacao@xbrain.com.br");
        usuarioImportacao.setEmail02("usuarioimportacao2@xbrain.com.br");
        usuarioImportacao.setEmail03("usuarioimportacao3@xbrain.com.br");
        return usuarioImportacao;
    }
}
