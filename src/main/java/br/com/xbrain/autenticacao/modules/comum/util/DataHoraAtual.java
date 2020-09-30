package br.com.xbrain.autenticacao.modules.comum.util;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class DataHoraAtual {

    public LocalDateTime getDataHora() {
        return LocalDateTime.now();
    }

    public LocalDateTime getDataHora(ZoneId zone) {
        return LocalDateTime.now(zone);
    }

    public LocalDate getData() {
        return LocalDate.now();
    }
}
