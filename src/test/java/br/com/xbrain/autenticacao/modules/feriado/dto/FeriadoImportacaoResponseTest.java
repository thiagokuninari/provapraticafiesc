package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FeriadoImportacaoResponseTest {

    @Test
    public void of_deveRetornarObjetoCorretoDeFeriado_quandoChamado() {
        assertThat(FeriadoImportacaoResponse.of(umFeriado()))
            .extracting("id", "nome", "dataFeriado", "dataCadastro", "feriadoNacional", "cidadeId",
                "cidadeNome", "estadoId", "estadoNome", "tipoFeriado", "motivoNaoImportacao", "feriadoImportadoComSucesso")
            .containsExactlyInAnyOrder(123, "FERIADO IMPORTADO", LocalDate.of(2020, 3, 22),
                LocalDateTime.of(2019, 11, 11, 11, 0, 0), Eboolean.F,
                null, null, 1, "PARANÁ", ETipoFeriado.ESTADUAL, List.of(), Eboolean.V);
    }

    @Test
    public void of_deveRetornarObjetoCorretoDeFeriadoImportacao_quandoChamado() {
        assertThat(FeriadoImportacaoResponse.of(umaFeriadoImportacao()))
            .extracting("id", "nome", "dataFeriado", "dataCadastro", "feriadoNacional", "cidadeId",
                "cidadeNome", "estadoId", "estadoNome", "tipoFeriado", "motivoNaoImportacao", "feriadoImportadoComSucesso")
            .containsExactlyInAnyOrder(null, "FERIADO NAO IMPORTADO", LocalDate.of(2020, 3, 22),
                null, null, 1, "LONDRINA", 1, "PARANÁ", ETipoFeriado.MUNICIPAL, List.of("Falha ao recuperar Cidade."),
                Eboolean.F);
    }

    @Test
    public void of_deveRetornarObjetoCorretoDeFeriado_quandoUfDoFeriadoForNulo() {
        var feriado = umFeriado();
        feriado.setUf(null);
        assertThat(FeriadoImportacaoResponse.of(feriado))
            .extracting("id", "nome", "dataFeriado", "dataCadastro", "feriadoNacional", "cidadeId",
                "cidadeNome", "estadoId", "estadoNome", "tipoFeriado", "motivoNaoImportacao", "feriadoImportadoComSucesso")
            .containsExactlyInAnyOrder(123, "FERIADO IMPORTADO", LocalDate.of(2020, 3, 22),
                LocalDateTime.of(2019, 11, 11, 11, 0, 0), Eboolean.F,
                null, null, null, null, ETipoFeriado.ESTADUAL, List.of(), Eboolean.V);
    }

    private Feriado umFeriado() {
        return Feriado.builder()
            .id(123)
            .nome("FERIADO IMPORTADO")
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

    private FeriadoImportacao umaFeriadoImportacao() {
        return FeriadoImportacao.builder()
            .tipoFeriado(ETipoFeriado.MUNICIPAL)
            .uf(Uf.builder()
                .id(1)
                .nome("PARANÁ")
                .uf("PR")
                .build())
            .cidade(Cidade.builder()
                .id(1)
                .nome("LONDRINA")
                .build())
            .dataFeriado(LocalDate.of(2020, 3, 22))
            .nome("FERIADO NAO IMPORTADO")
            .motivoNaoImportacao(List.of("Falha ao recuperar Cidade."))
            .build();
    }
}
