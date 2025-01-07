package br.com.xbrain.autenticacao.modules.feeder.dto;

import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.AGENTE_AUTORIZADO_SOCIO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.AGENTE_AUTORIZADO;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuario;
import static org.assertj.core.api.Assertions.assertThat;

public class VendedoresFeederResponseTest {

    @Test
    public void of_VendedoresFeederResponse_seUsuarioInativo() {
        assertThat(VendedoresFeederResponse.of(umUsuario(1, "NOME 1", I, null, AGENTE_AUTORIZADO)))
            .extracting("id", "nome", "situacao", "nivelCodigo")
            .containsExactly(1, "NOME 1 (INATIVO)", "I", "AGENTE_AUTORIZADO");
    }

    @Test
    public void of_VendedoresFeederResponse_seUsuarioRealocado() {
        assertThat(VendedoresFeederResponse.of(umUsuario(1, "NOME 1", R, null, AGENTE_AUTORIZADO)))
            .extracting("id", "nome", "situacao", "nivelCodigo")
            .containsExactly(1, "NOME 1 (REALOCADO)", "R", "AGENTE_AUTORIZADO");
    }

    @Test
    public void of_VendedoresFeederResponse_seUsuarioAtivo() {
        assertThat(VendedoresFeederResponse.of(umUsuario(1, "NOME 1", A, null, AGENTE_AUTORIZADO)))
            .extracting("id", "nome", "situacao", "nivelCodigo")
            .containsExactly(1, "NOME 1", "A", "AGENTE_AUTORIZADO");
    }

    @Test
    public void of_VendedoresFeederResponse_seUsuarioSocioPrincipal() {
        assertThat(VendedoresFeederResponse.of(umUsuario(1, "NOME 1", A, AGENTE_AUTORIZADO_SOCIO, AGENTE_AUTORIZADO)))
            .extracting("id", "nome", "situacao", "nivelCodigo")
            .containsExactly(1, "NOME 1 (SÓCIO PRINCIPAL)", "A", "AGENTE_AUTORIZADO");
    }

    @Test
    public void of_VendedoresFeederResponse_seUsuarioNomeNulo() {
        assertThat(VendedoresFeederResponse.of(umUsuario(1, null, A, AGENTE_AUTORIZADO_SOCIO, AGENTE_AUTORIZADO)))
            .extracting("id", "nome", "situacao", "nivelCodigo")
            .containsExactly(1, null, "A", "AGENTE_AUTORIZADO");
    }
}
