package br.com.xbrain.autenticacao.modules.usuario.dto;

import org.junit.Test;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;

import static org.assertj.core.api.Assertions.assertThat;

public class CidadeResponseTest {
    
    @Test
    public void of_deveRetornarCidadeResponse_seSolicitado() {
        assertThat(CidadeResponse.of(umaCidade()))
            .extracting("id", "nome", "uf.id", "uf.nome", "regional.id", "regional.nome")
            .containsExactly(5578, "LONDRINA", 1, "PARANA", 1027, "RPS");
    }

    private static Cidade umaCidade() {
        return Cidade.builder()
            .id(5578)
            .nome("LONDRINA")
            .uf(Uf.builder()
                .id(1)
                .nome("PARANA")
                .uf("PR")
                .build())
            .regional(Regional.builder()
                .id(1027)
                .nome("RPS")
                .situacao(ESituacao.A)
                .build())
            .build();
    }
}
