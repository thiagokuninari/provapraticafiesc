package br.com.xbrain.autenticacao.modules.usuario.dto;

import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.ADMINISTRADOR;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.doisUsuarioObjectArray;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioResponseTest {

    @Test
    public void ofUsuarioObjectArray_usuarioResponse_seSolicitado() {
        assertThat(UsuarioResponse.ofUsuarioObjectArray(doisUsuarioObjectArray(1, "NOME 1", ADMINISTRADOR)))
            .extracting("id", "nome", "codigoCargo")
            .containsExactly(1, "NOME 1", ADMINISTRADOR);
    }
}
