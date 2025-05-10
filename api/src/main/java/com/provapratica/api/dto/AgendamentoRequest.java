package com.provapratica.api.dto;

import com.provapratica.api.domain.Estudante;
import com.provapratica.api.domain.Professor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendamentoRequest {

    @NotBlank
    private LocalDateTime dataAgendamento;
    @NotNull
    private Estudante estudante;
    @NotNull
    private Professor professor;
    @NotBlank
    private String conteudo;
}
