package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FeriadoImportacaoTest {

    @Test
    public void isTipoFeriadoComUfObrigatorio_deveRetornarTrue_quandoTipoDoFeriadoEstadualOuMunicipal() {
        assertThat(umaFeriadoImportacao(ETipoFeriado.ESTADUAL).isTipoFeriadoComUfObrigatorio()).isTrue();
        assertThat(umaFeriadoImportacao(ETipoFeriado.MUNICIPAL).isTipoFeriadoComUfObrigatorio()).isTrue();
    }

    @Test
    public void isTipoFeriadoComUfObrigatorio_deveRetornarFalse_quandoTipoDoFeriadoNacionalOuNull() {
        assertThat(umaFeriadoImportacao(ETipoFeriado.NACIONAL).isTipoFeriadoComUfObrigatorio()).isFalse();
        assertThat(umaFeriadoImportacao(null).isTipoFeriadoComUfObrigatorio()).isFalse();
    }

    @Test
    public void isTipoFeriadoComCidadeObrigatorio_deveRetornarTrue_quandoTipoDoFeriadoMunicipal() {
        assertThat(umaFeriadoImportacao(ETipoFeriado.MUNICIPAL).isTipoFeriadoComCidadeObrigatorio()).isTrue();
    }

    @Test
    public void isTipoFeriadoComCidadeObrigatorio_deveRetornarFalse_quandoTipoDoFeriadoNaoMunicipal() {
        assertThat(umaFeriadoImportacao(ETipoFeriado.ESTADUAL).isTipoFeriadoComCidadeObrigatorio()).isFalse();
        assertThat(umaFeriadoImportacao(ETipoFeriado.NACIONAL).isTipoFeriadoComCidadeObrigatorio()).isFalse();
        assertThat(umaFeriadoImportacao(null).isTipoFeriadoComCidadeObrigatorio()).isFalse();
    }

    @Test
    public void isFeriadoNacional_deveRetornarTrue_quandoTipoDoFeriaodNacional() {
        assertThat(umaFeriadoImportacao(ETipoFeriado.NACIONAL).isFeriadoNacional()).isTrue();
    }

    @Test
    public void isFeriadoNacional_deveRetornarFalse_quandoTipoDoFeriaodNaoNacional() {
        assertThat(umaFeriadoImportacao(ETipoFeriado.MUNICIPAL).isFeriadoNacional()).isFalse();
        assertThat(umaFeriadoImportacao(ETipoFeriado.ESTADUAL).isFeriadoNacional()).isFalse();
        assertThat(umaFeriadoImportacao(null).isFeriadoNacional()).isFalse();
    }

    private FeriadoImportacao umaFeriadoImportacao(ETipoFeriado tipoFeriado) {
        return FeriadoImportacao.builder()
            .tipoFeriado(tipoFeriado)
            .dataFeriado(LocalDate.of(2019, 3, 22))
            .nome("FERIADO IMPORTADO")
            .motivoNaoImportacao(List.of())
            .build();
    }
}
