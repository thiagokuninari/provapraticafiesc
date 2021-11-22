package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioAtuacaoDto {
    private Integer id;
    private String diaSemana;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioInicio;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioFim;

    public static HorarioAtuacaoDto of(HorarioAtuacao response) {
        return HorarioAtuacaoDto.builder()
            .id(response.getId())
            .diaSemana(response.getDiaSemana().getNomeCompleto())
            .horarioInicio(response.getHorarioInicio())
            .horarioFim(response.getHorarioFim())
            .build();
    }
}
