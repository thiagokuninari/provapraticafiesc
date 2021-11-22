package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioAtuacaoDto {
    private Integer id;
    private String diaSemana;
    private String horarioInicio;
    private String horarioFim;

    public static HorarioAtuacaoDto of(HorarioAtuacao response) {
        return HorarioAtuacaoDto.builder()
            .id(response.getId())
            .diaSemana(response.getDiaSemana().getNomeCompleto())
            .horarioInicio(response.getHorarioInicio().toString())
            .horarioFim(response.getHorarioFim().toString())
            .build();
    }
}
