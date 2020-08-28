package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.service.HorarioAcessoAtivoLocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
@Profile("!producao")
@RequestMapping("api/public")
public class HorarioAcessoAtivoLocalController {

    @Autowired
    private HorarioAcessoAtivoLocalService horarioAcessoAtivoLocalService;

    @PutMapping("horario-acesso-ativo")
    public void alterarHorarioAcesso(@RequestParam @DateTimeFormat(pattern = "HH:mm:ss") LocalTime horarioInicioSabado,
                                     @RequestParam @DateTimeFormat(pattern = "HH:mm:ss") LocalTime horarioTerminoSabado,
                                     @RequestParam @DateTimeFormat(pattern = "HH:mm:ss") LocalTime horarioInicioSemanal,
                                     @RequestParam @DateTimeFormat(pattern = "HH:mm:ss") LocalTime horarioTerminoSemanal) {

        horarioAcessoAtivoLocalService.alterarHorariosAcesso(horarioInicioSabado, horarioTerminoSabado,
            horarioInicioSemanal, horarioTerminoSemanal);
    }
}
