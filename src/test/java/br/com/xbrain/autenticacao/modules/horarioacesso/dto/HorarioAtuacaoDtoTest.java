package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import br.com.xbrain.autenticacao.modules.horarioacesso.enums.EDiaSemana;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;
import org.junit.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class HorarioAtuacaoDtoTest {

    @Test
    public void of_deveRetornarHorarioAtuacaoFormatado() {
        var horarioAtuacao = umHorarioAtuacao();

        assertThat(HorarioAtuacaoDto.of(horarioAtuacao))
            .extracting("diaSemana", "horarioInicio", "horarioFim")
            .containsExactly("Segunda-Feira", "09:30", "16:30");
    }
    
    private HorarioAtuacao umHorarioAtuacao() {
        return HorarioAtuacao.builder()
            .id(1)
            .diaSemana(EDiaSemana.SEGUNDA)
            .horarioInicio(LocalTime.of(9, 30))
            .horarioFim(LocalTime.of(16, 30))
            .build();
    }
}
