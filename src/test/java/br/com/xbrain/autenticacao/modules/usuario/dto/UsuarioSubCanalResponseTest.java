package br.com.xbrain.autenticacao.modules.usuario.dto;

import org.junit.Test;

import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.XBRAIN;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal.INSIDE_SALES_PME;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanalDto;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanalInsideSales;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umCargo;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuario;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioSubCanalResponseTest {

    @Test
    public void of_deveRetornarUsuarioSubCanalResponse_seSolicitado() {
        var usuario = umUsuario();
        usuario.setCargo(umCargo());
        usuario.setSubCanais(Set.of(umSubCanalInsideSales()));

        assertThat(UsuarioSubCanalResponse.of(usuario))
            .extracting("id", "nome", "codigoNivel", "subCanais")
            .containsExactly(100, "NED STARK", XBRAIN, Set.of(umSubCanalDto(4, INSIDE_SALES_PME, "Inside Sales PME")));
    }
}
