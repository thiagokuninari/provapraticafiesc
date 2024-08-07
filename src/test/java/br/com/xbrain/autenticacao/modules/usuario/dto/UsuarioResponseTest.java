package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.AGENTE_AUTORIZADO;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioResponseTest {

    @Test
    public void of_deveRetornarUsuarioAgenteAutorizadoResponse_seSolicitado() {
        assertThat(UsuarioResponse.of(umUsuario()))
            .extracting("id", "nome", "email", "aaId", "tipoCanal")
            .containsExactly(100, "Fulano de Teste", "teste@teste.com", 101, ETipoCanal.PAP_PREMIUM);
    }

    @Test
    public void of_deveRetornarUsuarioAgenteAutorizadoResponse_seCanaisForemInformados() {
        var usuario = umUsuario();
        usuario.setCanais(Set.of(AGENTE_AUTORIZADO));

        assertThat(UsuarioResponse.of(usuario))
            .extracting("id", "nome", "email", "aaId", "tipoCanal", "canais")
            .containsExactly(100, "Fulano de Teste", "teste@teste.com", 101, ETipoCanal.PAP_PREMIUM,
                Set.of(AGENTE_AUTORIZADO));
    }

    @Test
    public void of_deveRetornarUsuarioAgenteAutorizadoResponse_seSubCanaisForemInformados() {
        var usuario = umUsuario();
        usuario.setSubCanais(Set.of(SubCanal.builder().codigo(ETipoCanal.PAP).nome("PAP").build()));

        assertThat(UsuarioResponse.of(usuario))
            .extracting("id", "nome", "email", "aaId", "tipoCanal", "subCanais")
            .containsExactly(100, "Fulano de Teste", "teste@teste.com", 101, ETipoCanal.PAP_PREMIUM,
                Set.of(SubCanalDto.builder().codigo(ETipoCanal.PAP).nome("PAP").build()));
    }

    private static Usuario umUsuario() {
        return Usuario
            .builder()
            .id(100)
            .nome("Fulano de Teste")
            .email("teste@teste.com")
            .agenteAutorizadoId(101)
            .tipoCanal(ETipoCanal.PAP_PREMIUM)
            .build();
    }
}
