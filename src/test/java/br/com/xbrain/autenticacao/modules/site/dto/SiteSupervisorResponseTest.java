package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSubordinadoDto;
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

    @Test
    public void of_SiteSupervisorResponse_seParametroUsuarioSubordinadoDto() {
        Assertions.assertThat(SiteSupervisorResponse.of(umUsuarioSubordinadoDto()))
            .extracting("id", "nome", "situacao", "coordenadoresIds")
            .containsExactly(1, "USUARIO DOIS", A, null);
    }

    private UsuarioSubordinadoDto umUsuarioSubordinadoDto() {
        return UsuarioSubordinadoDto.builder()
            .id(1)
            .nome("USUARIO DOIS")
            .situacao(A)
            .build();
    }
}
