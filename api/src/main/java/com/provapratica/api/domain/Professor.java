package com.provapratica.api.domain;

import com.provapratica.api.comun.CpfUtil;
import com.provapratica.api.dto.ProfessorRequest;
import com.provapratica.api.enums.EStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PROFESSOR")
public class Professor {

    @Id
    @SequenceGenerator(name = "SEQ_PROFESSOR", sequenceName = "SEQ_PROFESSOR", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PROFESSOR")
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CPF", nullable = false)
    private String cpf;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DATA_NASCIMENTO", nullable = false)
    private LocalDate dataNascimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_ESPECIALIDADE", referencedColumnName = "ID")
    private Especialidade especialidade;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private EStatus statusProfessor;

    public static Professor of(ProfessorRequest request, Especialidade especialidade) {
        return Professor.builder()
                .cpf(CpfUtil.removerCaracteresDoCpf(request.getCpf()))
                .nome(request.getNome())
                .dataNascimento(request.getDataNascimento())
                .especialidade(especialidade)
                .statusProfessor(EStatus.ATIVO)
                .build();
    }
}
