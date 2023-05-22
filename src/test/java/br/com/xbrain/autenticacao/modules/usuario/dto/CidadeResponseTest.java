package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper;
import br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioCidadeHelper;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CidadeResponseTest {

    @Test
    public void of_deveRetornarCidadeResponse_quandoSolicitado() {
        assertThat(CidadeResponse.of(CidadeHelper.cidadeLondrina()))
            .extracting("id", "nome", "codigoIbge", "uf.id", "uf.nome", "regional.id", "regional.nome", "fkCidade", "cidadePai")
            .containsExactly(5578, "LONDRINA", "4113700", 1, "PARANA", 1027, "RPS", null, null);
    }

    @Test
    public void definirNomeCidadePaiPorCidades_deveRetornarCidadeResponseComNomeCidadePai_quandoCidadeResponsePossuirFkCidade() {
        var response = CidadeResponse.of(CidadeHelper.distritoWarta());
        var cidades = List.of(CidadeHelper.cidadeLondrina());

        assertThat(CidadeResponse.definirNomeCidadePaiPorCidades(response, cidades))
            .extracting("id", "nome", "uf.id", "uf.nome", "regional.id", "regional.nome", "fkCidade", "cidadePai")
            .containsExactly(30910, "WARTA", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA");
    }

    @Test
    @SuppressWarnings("LineLength")
    public void definirNomeCidadePaiPorUsuarioCidades_deveRetornarCidadeResponseDeUsuarioCidadeComNomeCidadePai_quandoCidadeResponsePossuirFkCidade() {
        var response = CidadeResponse.of(CidadeHelper.distritoWarta());
        var cidades = Set.of(UsuarioCidadeHelper.usuarioCidadeLondrina());

        assertThat(CidadeResponse.definirNomeCidadePaiPorUsuarioCidades(response, cidades))
            .extracting("id", "nome", "uf.id", "uf.nome", "regional.id", "regional.nome", "fkCidade", "cidadePai")
            .containsExactly(30910, "WARTA", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA");
    }

    @Test
    public void getNomeComUf_deveRetornarNomeComUf_quandoUfNaoNull() {
        assertThat(CidadeResponse.of(CidadeHelper.cidadeLondrina()).getNomeComUf())
            .isEqualTo("LONDRINA - PR");
    }

    @Test
    public void getNomeComUf_deveRetornarNome_quandoUfNull() {
        var response = CidadeResponse.of(CidadeHelper.cidadeLondrina());
        response.setUf(null);

        assertThat(response.getNomeComUf())
            .isEqualTo("LONDRINA");
    }

    @Test
    public void getNomeComCidadePaiEUf_deveRetornarNomeComCidadePaiEUf_quandoCidadePaiNaoNull() {
        var response = CidadeResponse.of(CidadeHelper.distritoWarta());
        response.setCidadePai("LONDRINA");

        assertThat(response.getNomeComCidadePaiEUf())
            .isEqualTo("WARTA - LONDRINA - PR");
    }

    @Test
    public void getNomeComCidadePaiEUf_deveRetornarNomeComUf_quandoCidadePaiNull() {
        var response = CidadeResponse.of(CidadeHelper.cidadeLondrina());

        assertThat(response.getNomeComCidadePaiEUf())
            .isEqualTo("LONDRINA - PR");
    }
}
