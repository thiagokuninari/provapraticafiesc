package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.DiaAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.DiaAcessoHistorico;

import java.time.format.DateTimeFormatter;

import lombok.Builder;
import lombok.Data;

import static br.com.xbrain.autenticacao.modules.comum.enums.EFormatoDataHora.HORA;

@Data
@Builder
public class DiaAcessoResponse {

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(HORA.getDescricao());

    private String diaSemana;
    private String horarioInicial;
    private String horarioFinal;

    public static DiaAcessoResponse of(DiaAcesso request) {
        return DiaAcessoResponse.builder()
            .diaSemana(request.getDiaSemana().getNomeCompleto())
            .horarioInicial(request.getHorarioInicio().format(formatter))
            .horarioFinal(request.getHorarioFim().format(formatter))
            .build();
    }

    public static DiaAcessoResponse of(DiaAcessoHistorico request) {
        return DiaAcessoResponse.builder()
            .diaSemana(request.getDiaSemana().getNomeCompleto())
            .horarioInicial(request.getHorarioInicio().format(formatter))
            .horarioFinal(request.getHorarioFim().format(formatter))
            .build();
    }
}
