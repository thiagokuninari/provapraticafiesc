package com.provapratica.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudanteRequest {

    @NotBlank
    private String cpf;
    @NotBlank
    private String nome;
    private LocalDate dataNascimento;
    @NotBlank
    private String cep;
    @NotBlank
    private String logradouro;
    @NotBlank
    private String numero;
    @NotBlank
    private String bairro;
    @NotBlank
    private String estado;
    @NotBlank
    private String cidade;
    private String telefoneCelular;
    @NotBlank
    private String whatsapp;
    private String email;
}
