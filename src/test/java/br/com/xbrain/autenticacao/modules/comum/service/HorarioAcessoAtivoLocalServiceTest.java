package br.com.xbrain.autenticacao.modules.comum.service;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalTime;

@RunWith(SpringRunner.class)
@Import(HorarioAcessoAtivoLocalService.class)
public class HorarioAcessoAtivoLocalServiceTest {

    @Autowired
    private HorarioAcessoAtivoLocalService service;

    @Test
    public void isDentroHorarioPermitido_deveRetornarFalse_quandoNaoEstiverDentroDoHorario() {
        Assertions.assertThat(service.isDentroHorarioPermitidoNaSemana(LocalTime.of(9, 0, 0))).isFalse();
        Assertions.assertThat(service.isDentroHorarioPermitidoNaSemana(LocalTime.of(8, 59, 59))).isFalse();
        Assertions.assertThat(service.isDentroHorarioPermitidoNaSemana(LocalTime.of(21, 0, 0))).isFalse();
        Assertions.assertThat(service.isDentroHorarioPermitidoNaSemana(LocalTime.of(23, 0, 0))).isFalse();

        Assertions.assertThat(service.isDentroHorarioPermitidoNoSabado(LocalTime.of(10, 0, 0))).isFalse();
        Assertions.assertThat(service.isDentroHorarioPermitidoNoSabado(LocalTime.of(16, 0, 0))).isFalse();
    }

    @Test
    public void isDentroHorarioPermitido_deveRetornarTrue_quandoEstiverDentroDoHorario() {
        Assertions.assertThat(service.isDentroHorarioPermitidoNaSemana(LocalTime.of(9, 0, 1))).isTrue();
        Assertions.assertThat(service.isDentroHorarioPermitidoNaSemana(LocalTime.of(20, 59, 59))).isTrue();
        Assertions.assertThat(service.isDentroHorarioPermitidoNaSemana(LocalTime.of(10, 0, 0))).isTrue();

        Assertions.assertThat(service.isDentroHorarioPermitidoNoSabado(LocalTime.of(10, 0, 1))).isTrue();
        Assertions.assertThat(service.isDentroHorarioPermitidoNoSabado(LocalTime.of(15, 59, 59))).isTrue();
    }
}
