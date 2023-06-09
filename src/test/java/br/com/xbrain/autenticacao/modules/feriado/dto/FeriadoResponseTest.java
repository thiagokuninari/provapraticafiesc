package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static br.com.xbrain.autenticacao.modules.feriado.helper.FeriadoHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper.cidadeResponseAldeia;
import static org.assertj.core.api.Assertions.assertThat;

public class FeriadoResponseTest {

    @Test
    public void of_deveRetornarFeriadoResponse_quandoChamado() {
        assertThat(FeriadoResponse.of(umFeriadoMunicipalCidadeLondrina()))
            .extracting("id", "nome", "dataFeriado", "dataCadastro", "feriadoNacional", "cidadeId",
                "cidadeNome", "fkCidade", "cidadePai", "estadoId", "estadoNome", "tipoFeriado", "anoReferencia")
            .containsExactly(13853, "Aniversário da cidade", LocalDate.of(2023, 12, 10), LocalDateTime.of(2023, 1, 27, 16, 45),
                Eboolean.F, 5578, "LONDRINA", null, null, 1, "PARANA", ETipoFeriado.MUNICIPAL, 2023);
    }

    @Test
    public void of_deveRetornarFeriadoResponse_quandoFeriadoNaoPossuirEstadoECidadeAtrelados() {
        assertThat(FeriadoResponse.of(umFeriadoAnoNovo()))
            .extracting("id", "nome", "dataFeriado", "dataCadastro", "feriadoNacional", "cidadeId", "cidadeNome",
                "fkCidade", "cidadePai", "estadoId", "estadoNome", "tipoFeriado", "anoReferencia")
            .containsExactly(1, "Ano Novo", LocalDate.of(2024, 1, 1), LocalDateTime.of(2023, 1, 30, 10, 30),
                Eboolean.V, null, null, null, null, null, null, ETipoFeriado.NACIONAL, 2024);
    }

    @Test
    public void definirNomeCidadePaiPorDistritos_deveRetornarFeriadoResponseComNomeCidadePai_seFeriadoResponsePossuirFkCidade() {
        var response = FeriadoResponse.of(umFeriadoMunicipalDistritoAldeia());
        var distritos = Map.of(33618, cidadeResponseAldeia());

        assertThat(FeriadoResponse.definirNomeCidadePaiPorDistritos(response, distritos))
            .extracting("id", "nome", "dataFeriado", "dataCadastro", "feriadoNacional", "cidadeId",
                "cidadeNome", "fkCidade", "cidadePai", "estadoId", "estadoNome", "tipoFeriado", "anoReferencia")
            .containsExactly(34015, "Revolução Constitucionalista",
                LocalDate.of(2023, 7, 9), LocalDateTime.of(2023, 1, 27, 17, 30),
                Eboolean.F, 33618, "ALDEIA", 4864, "BARUERI", 2, "SAO PAULO", ETipoFeriado.MUNICIPAL, 2023);
    }
}
