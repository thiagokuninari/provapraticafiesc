package br.com.xbrain.autenticacao.modules.comum.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Objects;

@Slf4j
public class DateUtil {
    
    public static String dateTimeToString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return localDateTime.format(formatter);
    }

    public static LocalDate parseStringToLocalDate(String data) {
        LocalDate response = null;
        try {
            if (Objects.nonNull(data)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("pt", "BR"));
                response = LocalDate.parse(data, formatter);
            }
        } catch (DateTimeParseException ex) {
            log.error("Não foi possível converter a data " + data + " para o padrão dd/MM/yyyy");
        }

        return response;
    }

}
