package br.com.xbrain.autenticacao.modules.usuario.dto;

import org.junit.Test;

import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.XBRAIN;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal.PAP;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanalDto;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuario;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioSubCanalNivelResponseTest {

    @Test
    public void of_deveRetornarUsuarioSubCanalNivelResponse_seSolicitado() {
        var usuario = umUsuario();

        assertThat(UsuarioSubCanalNivelResponse.of(usuario))
            .extracting("id", "nome", "nivel", "subCanais")
            .containsExactly(100, "NED STARK", XBRAIN, Set.of(umSubCanalDto(1, PAP, "PAP")));
    }
}
