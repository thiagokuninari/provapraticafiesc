package com.provapratica.api.dto;

import com.provapratica.api.domain.Professor;
import com.provapratica.api.domain.Usuario;
import com.provapratica.api.enums.ENivelUsuario;
import com.provapratica.api.enums.EStatus;
import jakarta.persistence.*;
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
public class UsuarioResponse {

    private Integer id;
    private String cpf;
    private String nome;
    private LocalDate dataNascimento;
    private EStatus statusProfessor;
    private ENivelUsuario nivelUsuario;
    private String especialidade;
    private String cep;
    private String logradouro;
    private String numero;
    private String bairro;
    private String estado;
    private String cidade;
    private String telefoneCelular;
    private String whatsapp;
    private String email;

    public static UsuarioResponse of(Usuario usuario) {
        var usuarioReponse = new UsuarioResponse();
        if (usuario != null) {
            BeanUtils.copyProperties(usuario, usuarioReponse);
        }
        return usuarioReponse;
    }
}
