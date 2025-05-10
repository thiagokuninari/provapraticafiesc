package com.provapratica.api.domain;

import com.provapratica.api.comun.CpfUtil;
import com.provapratica.api.dto.EstudanteRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ESTUDANTE")
@EqualsAndHashCode(of = "id")
public class Estudante {

    @Id
    @SequenceGenerator(name = "SEQ_ESTUDANTE", sequenceName = "SEQ_ESTUDANTE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ESTUDANTE")
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CPF", nullable = false)
    private String cpf;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DATA_NASCIMENTO", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "CEP", nullable = false)
    private String cep;

    @Column(name = "LOGRADOURO", nullable = false)
    private String logradouro;

    @Column(name = "NUMERO", nullable = false)
    private String numero;

    @Column(name = "BAIRRO", nullable = false)
    private String bairro;

    @Column(name = "ESTADO", nullable = false)
    private String estado;

    @Column(name = "CIDADE", nullable = false)
    private String cidade;

    @Column(name = "TELEFONE_CELULAR")
    private String telefoneCelular;

    @Column(name = "WHATSAPP", nullable = false)
    private String whatsapp;

    @Column(name = "EMAIL")
    private String email;

    public static Estudante of(EstudanteRequest estudanteRequest) {
        return Estudante.builder()
                .cpf(CpfUtil.removerCaracteresDoCpf(estudanteRequest.getCpf()))
                .nome(estudanteRequest.getNome())
                .dataNascimento(estudanteRequest.getDataNascimento())
                .cep(estudanteRequest.getCep())
                .logradouro(estudanteRequest.getLogradouro())
                .numero(estudanteRequest.getNumero())
                .bairro(estudanteRequest.getBairro())
                .estado(estudanteRequest.getEstado())
                .cidade(estudanteRequest.getCidade())
                .telefoneCelular(estudanteRequest.getTelefoneCelular())
                .whatsapp(estudanteRequest.getWhatsapp())
                .email(estudanteRequest.getEmail())
                .build();
    }
}
