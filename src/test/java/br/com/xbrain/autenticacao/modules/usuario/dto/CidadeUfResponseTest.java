package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CidadeUfResponseTest {

    @Test
    public void of_deveRetornarCidadeUfResponse_quandoSolicitado() {
        assertThat(CidadeUfResponse.of(CidadeHelper.cidadeLondrina()))
            .extracting("cidadeId", "cidade", "uf", "ufSigla", "ufId", "fkCidade", "cidadePai")
            .containsExactly(5578, "LONDRINA", "PARANA", "PR", 1, null, null);
    }

    @Test
    public void definirNomeCidadePai_deveRetornarCidadeUfResponseComNomeCidadePai_quandoCidadeUfResponsePossuirFkCidade() {
        var response = CidadeUfResponse.of(CidadeHelper.distritoWarta());
        var cidades = List.of(CidadeHelper.cidadeLondrina());

        assertThat(CidadeUfResponse.definirNomeCidadePai(response, cidades))
            .extracting("cidadeId", "cidade", "uf", "ufSigla", "ufId", "fkCidade", "cidadePai")
            .containsExactly(30910, "WARTA", "PARANA", "PR", 1, 5578, "LONDRINA");
    }
}
