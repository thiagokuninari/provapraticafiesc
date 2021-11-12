package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcessoDia;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAcessoDiaHistorico;

import java.time.format.DateTimeFormatter;

import lombok.Builder;
import lombok.Data;

import static br.com.xbrain.autenticacao.modules.comum.enums.EFormatoDataHora.HORA;

@Data
@Builder
public class HorarioAcessoDiaDto {

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(HORA.getDescricao());

    private String diaSemana;
    private String horarioInicial;
    private String horarioFinal;

    public static HorarioAcessoDiaDto of(HorarioAcessoDia request) {
        return HorarioAcessoDiaDto.builder()
            .diaSemana(request.getDiaSemana().getNomeCompleto())
            .horarioInicial(request.getHorarioInicial().format(formatter))
            .horarioFinal(request.getHorarioFinal().format(formatter))
            .build();
    }

    public static HorarioAcessoDiaDto of(HorarioAcessoDiaHistorico request) {
        return HorarioAcessoDiaDto.builder()
            .diaSemana(request.getDiaSemana().getNomeCompleto())
            .horarioInicial(request.getHorarioInicio().format(formatter))
            .horarioFinal(request.getHorarioFim().format(formatter))
            .build();
    }
}
