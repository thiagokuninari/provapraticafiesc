package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import org.junit.Test;

import java.util.Map;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper.cidadeLondrina;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper.distritoWarta;
import static org.assertj.core.api.Assertions.assertThat;

public class CidadeResponseTest {

    @Test
    public void of_deveRetornarCidadeResponse_quandoSolicitado() {
        assertThat(CidadeResponse.of(cidadeLondrina()))
            .extracting("id", "nome", "codigoIbge", "netUno", "uf.id", "uf.nome",
                "regional.id", "regional.nome", "fkCidade", "cidadePai")
            .containsExactly(5578, "LONDRINA", "4113700", Eboolean.F, 1, "PARANA",
                1027, "RPS", null, null);
    }

    @Test
    public void definirNomeCidadePaiPorCidades_deveRetornarCidadeResponseComNomeCidadePai_seCidadeResponsePossuirFkCidade() {
        var response = CidadeResponse.of(distritoWarta());
        var distritos = Map.of(5578, cidadeLondrina());

        assertThat(CidadeResponse.definirNomeCidadePaiPorCidades(response, distritos))
            .extracting("id", "nome", "uf.id", "uf.nome", "regional.id", "regional.nome", "fkCidade", "cidadePai")
            .containsExactly(30910, "WARTA", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA");
    }

    @Test
    public void definirNomeCidadePaiPorDistritos_deveRetornarCidadeResponseComNomeCidadePai_seCidadeResponsePossuirFkCidade() {
        var response = CidadeResponse.of(distritoWarta());
        response.setCidadePai("LONDRINA");

        var distritos = Map.of(30910, response);

        assertThat(CidadeResponse.definirNomeCidadePaiPorDistritos(response, distritos))
            .extracting("id", "nome", "uf.id", "uf.nome", "regional.id", "regional.nome", "fkCidade", "cidadePai")
            .containsExactly(30910, "WARTA", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA");
    }

    @Test
    public void getNomeComUf_deveRetornarNomeComUf_quandoUfNaoNull() {
        assertThat(CidadeResponse.of(cidadeLondrina()).getNomeComUf())
            .isEqualTo("LONDRINA - PR");
    }

    @Test
    public void getNomeComUf_deveRetornarNome_quandoUfNull() {
        var response = CidadeResponse.of(cidadeLondrina());
        response.setUf(null);

        assertThat(response.getNomeComUf())
            .isEqualTo("LONDRINA");
    }

    @Test
    public void getNomeComCidadePaiEUf_deveRetornarNomeComCidadePaiEUf_quandoCidadePaiNaoNull() {
        var response = CidadeResponse.of(distritoWarta());
        response.setCidadePai("LONDRINA");

        assertThat(response.getNomeComCidadePaiEUf())
            .isEqualTo("WARTA - LONDRINA - PR");
    }

    @Test
    public void getNomeComCidadePaiEUf_deveRetornarNomeComUf_quandoCidadePaiNull() {
        var response = CidadeResponse.of(cidadeLondrina());

        assertThat(response.getNomeComCidadePaiEUf())
            .isEqualTo("LONDRINA - PR");
    }
}
