package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.service.HorarioAcessoAtivoLocalService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
@RequestMapping("api/horarios-acesso")
@RequiredArgsConstructor
public class HorarioAcessoAtivoLocalController {

    private final HorarioAcessoAtivoLocalService horarioAcessoAtivoLocalService;

    @PutMapping
    public void alterarHorarioAcesso(@RequestParam @DateTimeFormat(pattern = "HH:mm:ss") LocalTime horarioInicioSabado,
                                     @RequestParam @DateTimeFormat(pattern = "HH:mm:ss") LocalTime horarioTerminoSabado,
                                     @RequestParam @DateTimeFormat(pattern = "HH:mm:ss") LocalTime horarioInicioSemanal,
                                     @RequestParam @DateTimeFormat(pattern = "HH:mm:ss") LocalTime horarioTerminoSemanal) {

        horarioAcessoAtivoLocalService.alterarHorariosAcesso(horarioInicioSabado, horarioTerminoSabado,
            horarioInicioSemanal, horarioTerminoSemanal);
    }

    @GetMapping("status")
    public boolean isDentroHorarioPermitido() {
        return horarioAcessoAtivoLocalService.isDentroHorarioPermitido();
    }
}
