package br.com.xbrain.autenticacao.modules.comum.service;

import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@SuppressWarnings("MagicNumber")
public class HorarioAcessoAtivoLocalService {

    private LocalTime horarioInicioSabado = LocalTime.of(10, 0, 0);
    private LocalTime horarioTerminoSabado = LocalTime.of(16, 0, 0);
    private LocalTime horarioInicioSemanal = LocalTime.of(9, 0, 0);
    private LocalTime horarioTerminoSemanal = LocalTime.of(21, 0, 0);

    public boolean isDentroHorarioPermitidoNoSabado(LocalTime horario) {
        return horario.isAfter(horarioInicioSabado)
            && horario.isBefore(horarioTerminoSabado);
    }

    public boolean isDentroHorarioPermitidoNaSemana(LocalTime horario) {
        return horario.isAfter(horarioInicioSemanal)
            && horario.isBefore(horarioTerminoSemanal);
    }

    public void alterarHorariosAcesso(LocalTime horarioInicioSabado, LocalTime horarioTerminoSabado,
                                      LocalTime horarioInicioSemanal, LocalTime horarioTerminoSemanal) {
        this.horarioInicioSabado = horarioInicioSabado;
        this.horarioTerminoSabado = horarioTerminoSabado;
        this.horarioInicioSemanal = horarioInicioSemanal;
        this.horarioTerminoSemanal = horarioTerminoSemanal;
    }
}
