package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import br.com.xbrain.autenticacao.modules.horarioacesso.model.DiaAcesso;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.DiaAcessoHistorico;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class DiaAcessoResponse {

    private Integer horarioAcessoId;
    private Integer horarioHistoricoId;
    private String diaSemana;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioInicio;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioFim;

    public static DiaAcessoResponse of(DiaAcesso request) {
        return DiaAcessoResponse.builder()
            .horarioAcessoId(request.getHorarioAcesso().getId())
            .diaSemana(request.getDiaSemana().getNomeCompleto())
            .horarioInicio(request.getHorarioInicio())
            .horarioFim(request.getHorarioFim())
            .build();
    }

    public static DiaAcessoResponse of(DiaAcessoHistorico request) {
        return DiaAcessoResponse.builder()
            .horarioHistoricoId(request.getHorarioAcessoHistorico().getId())
            .diaSemana(request.getDiaSemana().getNomeCompleto())
            .horarioInicio(request.getHorarioInicio())
            .horarioFim(request.getHorarioFim())
            .build();
    }
}
