package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.ADMINISTRADOR;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.XBRAIN;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal.PAP;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanal;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.umSubCanalDto;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioSubCanalNivelResponseTest {

    @Test
    public void of_deveRetornarUsuarioSubCanalNivelResponse_seSolicitado() {
        var usuario = umUsuario();

        assertThat(UsuarioSubCanalNivelResponse.of(usuario))
            .extracting("id", "nome", "nivel", "subCanais")
            .containsExactly(100, "NED STARK", XBRAIN, List.of(umSubCanalDto(1, PAP, "PAP")));
    }

    private static Usuario umUsuario() {
        return Usuario
            .builder()
            .id(100)
            .nome("NED STARK")
            .cargo(umCargo())
            .subCanais(Set.of(umSubCanal()))
            .build();
    }

    private static Cargo umCargo() {
        return Cargo
            .builder()
            .id(50)
            .codigo(ADMINISTRADOR)
            .nome("ADMINISTRADOR")
            .situacao(A)
            .nivel(umNivelXbrain())
            .build();
    }

    private static Nivel umNivelXbrain() {
        return Nivel
            .builder()
            .id(4)
            .codigo(XBRAIN)
            .nome("X-BRAIN")
            .situacao(A)
            .build();
    }
}
