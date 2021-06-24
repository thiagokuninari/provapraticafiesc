package br.com.xbrain.autenticacao.modules.comum.util;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DiasUteisAdjusterTest {

    @Test
    public void with_deveRetornarDataDoDiaSeguinte_quandoNaoTiverForDeSemanaOuFeriado() {
        var dataOriginal = LocalDateTime.of(2020, 8, 4, 10, 0, 0);
        assertThat(dataOriginal.with(new DiasUteisAdjuster(1, umaListaDeDatasDeFeriados())))
            .isEqualTo(LocalDateTime.of(2020, 8, 5, 10, 0, 0));
    }

    @Test
    public void with_deveRetornarDataDaProximaSegunda_quandoDataOriginalForSexta() {
        var dataOriginal = LocalDateTime.of(2020, 8, 7, 10, 0, 0);

        assertThat(dataOriginal.with(new DiasUteisAdjuster(1, umaListaDeDatasDeFeriados())))
            .isEqualTo(LocalDateTime.of(2020, 8, 10, 10, 0, 0));
    }

    @Test
    public void with_deveRetornarDataComDoisDiasMais_quandoDiaSeguinteForFeriado() {
        var dataOriginal = LocalDateTime.of(2020, 8, 11, 10, 0, 0);

        assertThat(dataOriginal.with(new DiasUteisAdjuster(1, umaListaDeDatasDeFeriados())))
            .isEqualTo(LocalDateTime.of(2020, 8, 13, 10, 0, 0));
    }

    @Test
    public void with_deveRetornarDataDaProximaTerca_quandoDataOriginalForNoSabado() {
        var dataOriginal = LocalDateTime.of(2020, 8, 1, 10, 0, 0);

        assertThat(dataOriginal.with(new DiasUteisAdjuster(1, umaListaDeDatasDeFeriados())))
            .isEqualTo(LocalDateTime.of(2020, 8, 4, 10, 0, 0));
    }

    @Test
    public void with_deveRetornarDataDaProximaTerca_quandoDataOriginalForNoDomingoEForAdicionadoTresDias() {
        var dataOriginal = LocalDateTime.of(2020, 8, 2, 10, 0, 0);

        assertThat(dataOriginal.with(new DiasUteisAdjuster(1, umaListaDeDatasDeFeriados())))
            .isEqualTo(LocalDateTime.of(2020, 8, 4, 10, 0, 0));
    }

    @Test
    public void with_deveRetornarDataCorreto_quandoForAdicionadoMaisDias() {
        var dataOriginal = LocalDateTime.of(2020, 8, 7, 10, 0, 0);

        assertThat(dataOriginal.with(new DiasUteisAdjuster(10, umaListaDeDatasDeFeriados())))
            .isEqualTo(LocalDateTime.of(2020, 8, 25, 10, 0, 0));
    }

    private List<LocalDate> umaListaDeDatasDeFeriados() {
        return List.of(LocalDate.of(2020, 8, 8),
            LocalDate.of(2020, 8, 12),
            LocalDate.of(2020, 8, 20));
    }
}
