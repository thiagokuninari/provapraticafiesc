package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class UsuarioCidadeDtoTest {

    @Test
    public void of_deveRetornarUsuarioCidadeDtoDeCidade_quandoSolicitado() {
        assertThat(UsuarioCidadeDto.of(CidadeHelper.cidadeLondrina()))
            .extracting("idCidade", "nomeCidade", "idUf", "nomeUf", "idRegional", "nomeRegional", "fkCidade", "cidadePai")
            .containsExactly(5578, "LONDRINA", 1, "PARANA", 1027, "RPS", null, null);
    }

    @Test
    public void of_deveRetornarUsuarioCidadeDtoDeCidadeResponse_quandoSolicitado() {
        assertThat(UsuarioCidadeDto.of(CidadeHelper.cidadeResponseLondrina()))
            .extracting("idCidade", "nomeCidade", "idUf", "nomeUf", "idRegional", "nomeRegional", "fkCidade", "cidadePai")
            .containsExactly(5578, "LONDRINA", 1, "PARANA", 1027, "RPS", null, null);
    }

    @Test
    public void of_deveRetornarListaUsuarioCidadeDto_quandoSolicitado() {
        assertThat(UsuarioCidadeDto.of(CidadeHelper.listaCidadesDeSaoPaulo()))
            .extracting("idCidade", "nomeCidade", "idUf", "nomeUf", "idRegional", "nomeRegional", "fkCidade", "cidadePai")
            .containsExactly(
                tuple(33618, "ALDEIA", 2, "SAO PAULO", 1031, "RSI", 4864, "BARUERI"),
                tuple(4870, "BERNARDINO DE CAMPOS", 2, "SAO PAULO", 1031, "RSI", null, null),
                tuple(4943, "COSMOPOLIS", 2, "SAO PAULO", 1031, "RSI", null, null),
                tuple(4944, "COSMORAMA", 2, "SAO PAULO", 1031, "RSI", null, null),
                tuple(33252, "JARDIM BELVAL", 2, "SAO PAULO", 1031, "RSI", 4864, "BARUERI"),
                tuple(33255, "JARDIM SILVEIRA", 2, "SAO PAULO", 1031, "RSI", 4864, "BARUERI"),
                tuple(33269, "JORDANESIA", 2, "SAO PAULO", 1031, "RSI", 4903, "CAJAMAR"),
                tuple(5107, "LINS", 2, "SAO PAULO", 1031, "RSI", null, null),
                tuple(5128, "MARILIA", 2, "SAO PAULO", 1031, "RSI", null, null),
                tuple(33302, "POLVILHO", 2, "SAO PAULO", 1031, "RSI", 4903, "CAJAMAR"),
                tuple(4864, "BARUERI", 2, "SAO PAULO", 1030, "RSC", null, null),
                tuple(4903, "CAJAMAR", 2, "SAO PAULO", 1030, "RSC", null, null)
            );
    }

    @Test
    @SuppressWarnings("LineLength")
    public void definirNomeCidadePaiPorCidades_deveRetornarUsuarioCidadeDtoDeCidadeComNomeCidadePai_quandoUsuarioCidadeResponsePossuirFkCidade() {
        var response = UsuarioCidadeDto.of(CidadeHelper.distritoWarta());
        var cidades = List.of(CidadeHelper.cidadeLondrina());

        assertThat(UsuarioCidadeDto.definirNomeCidadePaiPorCidades(response, cidades))
            .extracting("idCidade", "nomeCidade", "idUf", "nomeUf", "idRegional", "nomeRegional", "fkCidade", "cidadePai")
            .containsExactly(30910, "WARTA", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA");
    }
}
