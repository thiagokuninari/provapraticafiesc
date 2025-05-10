package com.provapratica.api.dto;

import com.provapratica.api.domain.Estudante;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudanteResponse {

    private Integer id;
    private String cpf;
    private String nome;
    private LocalDate dataNascimento;
    private String cep;
    private String logradouro;
    private String numero;
    private String bairro;
    private String estado;
    private String cidade;
    private String telefoneCelular;
    private String whatsapp;
    private String email;

    public static EstudanteResponse of(Estudante estudante) {
        var estudanteResponse = new EstudanteResponse();
        BeanUtils.copyProperties(estudante, estudanteResponse);

        return estudanteResponse;
    }

    public EstudanteResponse(String nome, String whatsapp) {
        this.nome = nome;
        this.whatsapp = whatsapp;
    }
}
