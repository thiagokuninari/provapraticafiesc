package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioDistribuicaoResponseTest {

    @Test
    public void of_deveRetornarUsuarioDistribuicaoResponse_quandoDesejarConverterUmaEquipeVendaUsuarioResponse() {
        var actual = UsuarioDistribuicaoResponse.of(umaEquipeVendaUsuarioResponse());

        var expected = UsuarioDistribuicaoResponse
            .builder()
            .id(1)
            .nome("RENATO")
            .build();

        assertThat(actual).isEqualTo(expected);
    }

    private EquipeVendaUsuarioResponse umaEquipeVendaUsuarioResponse() {
        return EquipeVendaUsuarioResponse.builder()
            .usuarioId(1)
            .usuarioNome("RENATO")
            .build();
    }
}