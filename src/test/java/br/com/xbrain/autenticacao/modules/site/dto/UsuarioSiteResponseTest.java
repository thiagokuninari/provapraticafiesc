package br.com.xbrain.autenticacao.modules.site.dto;

import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.OPERACAO_TELEVENDAS;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioResponseHelper.umUsuarioResponse;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioSiteResponseTest {

    @Test
    public void of_usuarioSiteResponse_seSolicitado() {
        assertThat(UsuarioSiteResponse.of(umUsuarioResponse(1, "USUARIO NOME", A, OPERACAO_TELEVENDAS)))
            .extracting("usuarioId", "usuarioNome", "cargoNome")
            .containsExactly(1, "USUARIO NOME", "OPERACAO_TELEVENDAS");
    }
}
