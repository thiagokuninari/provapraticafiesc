package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SiteSupervisorResponseTest {

    @Test
    public void of_siteSupervisorResponse_quandoConverterUmUsuarioSupervisor() {
        assertThat(SiteSupervisorResponse.of(umUsuario()))
            .isEqualTo(SiteSupervisorResponse.builder()
                .id(1)
                .nome("RENATO")
                .build());
    }

    private Usuario umUsuario() {
        return Usuario.builder()
            .id(1)
            .nome("RENATO")
            .build();
    }
}