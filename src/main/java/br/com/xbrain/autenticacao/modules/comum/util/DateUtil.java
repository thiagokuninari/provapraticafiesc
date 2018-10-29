package br.com.xbrain.autenticacao.modules.comum.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

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
        if (Objects.nonNull(data)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("pt", "BR"));
            response = LocalDate.parse(data, formatter);
        }
        return response;
    }

}
