package br.com.xbrain.autenticacao.modules.comum.util;

import br.com.xbrain.autenticacao.modules.comum.enums.EFormatoDataHora;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class DateUtil {

    private static final Locale PT_BR = new Locale("pt", "BR");

    public static LocalDate strToDate(String data, String patter) {
        var formatter = DateTimeFormatter.ofPattern(patter);
        formatter = formatter.withLocale(Locale.getDefault());
        return LocalDate.parse(data, formatter);
    }

    public static LocalDate strToDate(String data) {
        return strToDate(data, EFormatoDataHora.DATA_BANCO.getDescricao());
    }

    public static void validarPeriodoMaximo(String dataInicial, String dataFinal, Integer qtdMaximaDias) {
        if (ChronoUnit.DAYS.between(strToDate(dataInicial), strToDate(dataFinal)) > qtdMaximaDias) {
            throw new ValidacaoException("O período não deve ser superior a " + qtdMaximaDias + " dias.");
        }
    }

    public static void validarPeriodoMaximo(LocalDate dataInicial, LocalDate dataFinal, Integer qtdMaximaDias) {
        if (ChronoUnit.DAYS.between(dataInicial, dataFinal) > qtdMaximaDias) {
            throw new ValidacaoException("O período não deve ser superior a " + qtdMaximaDias + " dias.");
        }
    }

    public static String formatarDataHora(EFormatoDataHora format, LocalDateTime data) {
        var response = "";
        if (!ObjectUtils.isEmpty(data)) {
            var formatter = DateTimeFormatter.ofPattern(format.getDescricao(), PT_BR);
            response = data.format(formatter);
        }
        return response;
    }

    public static void validarDataInicialPosteriorDataFinal(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial.isAfter(dataFinal)) {
            throw new ValidacaoException("Data inicial não pode ser posterior a data final.");
        }
    }

    public static void validarDataFinalPosteriorAAtual(LocalDate data) {
        if (data.isAfter(LocalDate.now())) {
            throw new ValidacaoException("Data final não pode ser posterior a atual.");
        }
    }
}
