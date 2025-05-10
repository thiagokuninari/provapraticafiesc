package com.provapratica.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspecialidadeRequest {

    @NotBlank
    private String nomeEspecialidade;
    @NotBlank
    private String codigoEspecialidade;
}
