package com.provapratica.api.dto;

import com.provapratica.api.domain.Agendamento;
import com.provapratica.api.enums.EStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendamentoResponse {

    private Integer id;
    private LocalDateTime dataAgendamento;
    private Integer estudanteId;
    private String estudanteNome;
    private Integer professorId;
    private String professorNome;
    private String conteudo;
    private EStatus statusEspecialidade;

    public static AgendamentoResponse of(Agendamento agendamento) {
        var agendamentoResponse = new AgendamentoResponse();
        if (agendamento != null) {
            BeanUtils.copyProperties(agendamento, agendamentoResponse);
            agendamentoResponse.setEstudanteId(agendamento.getEstudante().getId());
            agendamentoResponse.setEstudanteNome(agendamento.getEstudante().getNome());
            agendamentoResponse.setProfessorId(agendamento.getProfessor().getId());
            agendamentoResponse.setProfessorNome(agendamento.getProfessor().getNome());
        }
        return agendamentoResponse;
    }
}
