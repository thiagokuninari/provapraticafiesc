package com.provapratica.api.domain;

import com.provapratica.api.dto.*;
import com.provapratica.api.enums.EStatusAgendamento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "AGENDAMENTO")
@EqualsAndHashCode(of = "id")
public class Agendamento {

    @Id
    @SequenceGenerator(name = "SEQ_AGENDAMENTO", sequenceName = "SEQ_AGENDAMENTO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_AGENDAMENTO")
    @Column(name = "ID")
    private Integer id;

    @Column(name = "DATA_AGENDAMENTO", nullable = false)
    private LocalDateTime dataAgendamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_ESTUDANTE", referencedColumnName = "ID")
    private Estudante estudante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_PROFESSOR", referencedColumnName = "ID")
    private Professor professor;

    @Column(name = "CONTEUDO", nullable = false)
    private String conteudo;

    @Column(name = "STATUS_AGENDAMENTO", nullable = false)
    @Enumerated(EnumType.STRING)
    private EStatusAgendamento statusEspecialidade;

    public static Agendamento of(AgendamentoRequest agendamentoRequest) {
        return Agendamento.builder()
                .dataAgendamento(agendamentoRequest.getDataAgendamento())
                .estudante(agendamentoRequest.getEstudante())
                .professor(agendamentoRequest.getProfessor())
                .conteudo(agendamentoRequest.getConteudo())
                .statusEspecialidade(EStatusAgendamento.EM_ANDAMENTO)
                .build();
    }
}
