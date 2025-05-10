package com.provapratica.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.provapratica.api.domain.Nivel;
import com.provapratica.api.enums.ENivelUsuario;
import com.provapratica.api.enums.EStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRequest {

    @NotBlank(message = "O CPF é obrigatório.")
    private String cpf;
    @NotBlank(message = "O nome é obrigatório.")
    private String nome;
    @NotNull(message = "A data de nascimento é obrigatória.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;
    @NotNull(message = "O nível do usuário é obrigatório.")
    private Nivel nivel;
    @Email(message = "O e-mail informado é inválido.")
    private String email;
    private String especialidade;
    private String cep;
    private String logradouro;
    private String numero;
    private String bairro;
    private String estado;
    private String cidade;
    private String telefoneCelular;
    private String whatsapp;
}
