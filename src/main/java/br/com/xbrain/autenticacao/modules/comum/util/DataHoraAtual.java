package br.com.xbrain.autenticacao.modules.comum.util;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DataHoraAtual {

    public LocalDateTime getDataHora() {
        return LocalDateTime.now();
    }

    public LocalDate getData() {
        return LocalDate.now();
    }
}
