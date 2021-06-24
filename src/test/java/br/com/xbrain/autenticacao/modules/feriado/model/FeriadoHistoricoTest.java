package br.com.xbrain.autenticacao.modules.feriado.model;

import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class FeriadoHistoricoTest {

    @Test
    public void of_deveRetornarFeriadoHistoricoCorreto_quandoChamado() {
        assertThat(FeriadoHistorico.of(umFeriadoNacional(),"EDITADO", 2222))
            .extracting("feriado.id", "observacao", "usuario.id")
            .containsExactlyInAnyOrder(1234, "EDITADO", 2222);
    }

    private Feriado umFeriadoNacional() {
        return Feriado.builder()
            .id(1234)
            .nome("FERIADO NACIONAL")
            .dataFeriado(LocalDate.of(2019, 9, 23))
            .dataCadastro(LocalDateTime.of(2018, 11, 11, 11, 11, 11))
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .build();
    }
}
