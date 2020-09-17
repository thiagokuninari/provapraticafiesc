package br.com.xbrain.autenticacao.modules.comum.util;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class DiasUteisAdjuster implements TemporalAdjuster {

    private final int prazoDias;
    private final Set<DayOfWeek> finalSemana = Sets.newHashSet(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    private final List<LocalDate> feriados;

    @Override
    public Temporal adjustInto(Temporal temporal) {
        var dataOriginal = LocalDateTime.from(temporal);
        return getProximaDiaUtil(verificarDataOriginal(dataOriginal), prazoDias);
    }

    private LocalDateTime getProximaDiaUtil(LocalDateTime data, int diasUteis) {
        if (diasUteis > 0) {
            do {
                data = data.plusDays(1);
            }
            while (verificarData(data, diasUteis));

            return getProximaDiaUtil(data, diasUteis - 1);
        } else {
            return data;
        }
    }

    private LocalDateTime verificarDataOriginal(LocalDateTime data) {
        while (isFinalSemanaOrFeriado(data)) {
            data = data.plusDays(1);
        }
        return data;
    }

    private boolean verificarData(LocalDateTime data , int dia) {
        return isFinalSemanaOrFeriado(data) && dia != 0;
    }

    private boolean isFinalSemanaOrFeriado(LocalDateTime data) {
        return finalSemana.contains(data.getDayOfWeek())
            || feriados.contains(data.toLocalDate());
    }
}
