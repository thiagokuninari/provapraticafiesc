package com.provapratica.api.dto;

import com.provapratica.api.domain.Professor;
import com.provapratica.api.enums.EStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessorResponse {

    private Integer id;
    private String cpf;
    private String nome;
    private LocalDate dataNascimento;
    private Integer especialidadeId;
    private String especialidadeNome;
    private EStatus statusProfessor;

    public static ProfessorResponse of(Professor professor) {
        var professorResponse = new ProfessorResponse();
        if (professor != null) {
            BeanUtils.copyProperties(professor, professorResponse);
            professorResponse.setEspecialidadeId(professor.getEspecialidade().getId());
            professorResponse.setEspecialidadeNome(professor.getEspecialidade().getNomeEspecialidade());
        }
        return professorResponse;
    }
}
