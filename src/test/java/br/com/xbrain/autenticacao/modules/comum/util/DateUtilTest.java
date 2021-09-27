package br.com.xbrain.autenticacao.modules.comum.util;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.LocalDate;

import static br.com.xbrain.autenticacao.modules.comum.util.DateUtil.*;
import static org.assertj.core.api.Assertions.assertThatCode;

public class DateUtilTest {

    @Test
    public void validarPeriodoMaximo_validacaoException_seMaiorQueLimiteMaximo() {
        Assertions.assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> validarPeriodoMaximo(LocalDate.now(), LocalDate.now().plusDays(31), 30))
            .withMessage("O período não deve ser superior a 30 dias.");
    }

    @Test
    public void validarPeriodoMaximo_naoDeveDispararException_seIgualALimiteMaximo() {
        assertThatCode(() -> validarPeriodoMaximo(LocalDate.now(), LocalDate.now().plusDays(30), 30))
            .doesNotThrowAnyException();
    }

    @Test
    public void validarPeriodoMaximo_naoDeveDispararException_seMenorQueLimiteMaximo() {
        assertThatCode(() -> validarPeriodoMaximo(LocalDate.now(), LocalDate.now().plusDays(29), 30))
            .doesNotThrowAnyException();
    }

    @Test
    public void validarDataInicialPosteriorDataFinal_validacaoException_seDataInicialPosteriorADataFinal() {
        Assertions.assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> validarDataInicialPosteriorDataFinal(LocalDate.now().plusDays(1), LocalDate.now()))
            .withMessage("Data inicial não pode ser posterior a data final.");
    }

    @Test
    public void validarDataInicialPosteriorDataFinal_naoDeveDispararException_seDataInicialIgualADataFinal() {
        assertThatCode(() -> validarDataInicialPosteriorDataFinal(LocalDate.now(), LocalDate.now()))
            .doesNotThrowAnyException();
    }

    @Test
    public void validarDataInicialPosteriorDataFinal_naoDeveDispararException_seDataInicialAnteriorADataFinal() {
        assertThatCode(() -> validarDataInicialPosteriorDataFinal(LocalDate.now().minusDays(1), LocalDate.now()))
            .doesNotThrowAnyException();
    }

    @Test
    public void validarDataFinalPosteriorAAtual_validacaoException_seDataFinalPosteriorAHoje() {
        Assertions.assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> validarDataFinalPosteriorAAtual(LocalDate.now().plusDays(1)))
            .withMessage("Data final não pode ser posterior a atual.");
    }

    @Test
    public void validarDataInicialPosteriorDataFinal_naoDeveDispararException_seDataFinalIgualAHoje() {
        assertThatCode(() -> validarDataFinalPosteriorAAtual(LocalDate.now()))
            .doesNotThrowAnyException();
    }

    @Test
    public void validarDataInicialPosteriorDataFinal_naoDeveDispararException_seDataFinalAnteriorAHoje() {
        assertThatCode(() -> validarDataFinalPosteriorAAtual(LocalDate.now().minusDays(1)))
            .doesNotThrowAnyException();
    }
}
