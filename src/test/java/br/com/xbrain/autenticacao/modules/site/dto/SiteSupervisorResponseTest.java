package br.com.xbrain.autenticacao.modules.site.dto;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.doisUsuario;

public class SiteSupervisorResponseTest {

    @Test
    public void of_SiteSupervisorResponse_seParametroUsuarioEListInteger() {
        Assertions.assertThat(SiteSupervisorResponse.of(doisUsuario(1, "USUARIO UM", A), List.of(2, 3, 4)))
            .extracting("id", "nome", "situacao", "coordenadoresIds")
            .containsExactly(1, "USUARIO UM", A, List.of(2, 3, 4));
    }
}
