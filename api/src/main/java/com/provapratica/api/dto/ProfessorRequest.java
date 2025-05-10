package com.provapratica.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessorRequest {

    @NotBlank
    private String cpf;
    @NotBlank
    private String nome;
    private LocalDate dataNascimento;
    private Integer especialidadeId;
}