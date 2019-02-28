package br.com.xbrain.autenticacao.modules.comum.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

// Utilizar lib xbrainUtils, classe DateUtils
@Deprecated
@Slf4j
public class DateUtil {

    static final Locale LOCALE_PT_BR = new Locale("pt", "BR");

    public static String parseLocalDateTimeToString(LocalDateTime localDateTime) {
        try {
            if (!ObjectUtils.isEmpty(localDateTime)) {
                return localDateTime.format(getDateTimeFormatter());
            }
        } catch (DateTimeParseException ex) {
            log.error("Não foi possível converter a data " + localDateTime + " para o padrão dd/MM/yyyy HH:mm");
        }

        return "";
    }

    public static String parseLocalDateToString(LocalDate localDate) {
        try {
            if (!ObjectUtils.isEmpty(localDate)) {
                return localDate.format(getDateFormatter());
            }
        } catch (DateTimeParseException ex) {
            log.error("Não foi possível converter a data " + localDate + " para o padrão dd/MM/yyyy");
        }

        return "";
    }

    public static LocalDate parseStringToLocalDate(String data) {
        try {
            if (!ObjectUtils.isEmpty(data)) {
                return LocalDate.parse(data, getDateFormatter());
            }
        } catch (DateTimeParseException ex) {
            log.error("Não foi possível converter a data " + data + " para o padrão dd/MM/yyyy");
        }

        return null;
    }

    private static DateTimeFormatter getDateFormatter() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy", LOCALE_PT_BR);
    }

    private static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", LOCALE_PT_BR);
    }

}
