package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class FeriadoResponseTest {

    @Test
    public void of_deveRetornarObjetoCorreto_quandoChamado() {
        assertThat(FeriadoResponse.of(umFeriado()))
            .extracting("id", "nome", "dataFeriado", "dataCadastro", "feriadoNacional", "cidadeId",
                "cidadeNome", "estadoId", "estadoNome", "tipoFeriado", "anoReferencia")
            .containsExactlyInAnyOrder(123, "ANIVERSARIO DA VIOLA", LocalDate.of(2020, 3, 22),
                LocalDateTime.of(2019, 11, 11, 11, 0, 0), Eboolean.F,
                null, null, 1, "PARANÁ", ETipoFeriado.ESTADUAL, 2020);
    }

    private Feriado umFeriado() {
        return Feriado.builder()
            .id(123)
            .nome("ANIVERSARIO DA VIOLA")
            .dataCadastro(LocalDateTime.of(2019, 11, 11, 11, 0, 0))
            .dataFeriado(LocalDate.of(2020, 3, 22))
            .feriadoNacional(Eboolean.F)
            .tipoFeriado(ETipoFeriado.ESTADUAL)
            .situacao(ESituacaoFeriado.ATIVO)
            .uf(Uf.builder()
                .id(1)
                .nome("PARANÁ")
                .uf("PR")
                .build())
            .build();
    }
}
