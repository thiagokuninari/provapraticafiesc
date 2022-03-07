package br.com.xbrain.autenticacao.modules.solicitacaoramal.util;

import br.com.xbrain.autenticacao.modules.feriado.model.FeriadoSingleton;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;

import static java.time.DayOfWeek.SUNDAY;

public class SolicitacaoRamalExpiracaoAdjuster implements TemporalAdjuster {

    private static final int EXPIRACAO_SOLICITACAO_RAMAL_EM_HORAS = 72;
    private final DayOfWeek finalSemana = SUNDAY;

    @Override
    public Temporal adjustInto(Temporal temporal) {
        LocalDateTime dataCadastro = LocalDateTime.from(temporal);
        return getProximaHoraUtil(verificarDataCadastro(dataCadastro), EXPIRACAO_SOLICITACAO_RAMAL_EM_HORAS);
    }

    private LocalDateTime getProximaHoraUtil(LocalDateTime data, int horasUteis) {
        if (horasUteis > 0) {
            do {
                data = data.plusHours(1);
            }
            while (verificaData(data, horasUteis - 1));

            return getProximaHoraUtil(data, horasUteis - 1);
        } else {
            return data;
        }
    }

    private boolean verificaData(LocalDateTime data, int hora) {
        return isFinalDeSemanaOuFeriado(data) && hora != 0;
    }

    private LocalDateTime verificarDataCadastro(LocalDateTime data) {
        if (isFinalDeSemanaOuFeriado(data)) {
            do {
                data = data.plusDays(1);
            }
            while (isFinalDeSemanaOuFeriado(data));
        }
        return data;
    }

    private boolean isFinalDeSemanaOuFeriado(LocalDateTime data) {
        return finalSemana.equals(data.getDayOfWeek())
                || FeriadoSingleton.getInstance().getFeriadosNacionais().contains(data.toLocalDate());
    }
}
