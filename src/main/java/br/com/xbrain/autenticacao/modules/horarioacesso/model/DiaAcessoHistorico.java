package br.com.xbrain.autenticacao.modules.horarioacesso.model;

import java.time.LocalTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import br.com.xbrain.autenticacao.modules.comum.enums.EDiaSemana;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DIA_ACESSO_HIST")
public class DiaAcessoHistorico {

    @Id
    @SequenceGenerator(name = "SEQ_DIA_ACESSO_HIST", sequenceName = "SEQ_DIA_ACESSO_HIST", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_DIA_ACESSO_HIST", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_HORARIO_ACESSO_HIST", referencedColumnName = "ID", nullable = false)
    private HorarioAcessoHistorico horarioAcessoHistorico;

    @NotNull
    @Column(name = "DIA_SEMANA")
    @Enumerated(EnumType.STRING)
    private EDiaSemana diaSemana;

    @NotNull
    @Column(name = "HORARIO_INICIO")
    private LocalTime horarioInicio;

    @NotNull
    @Column(name = "HORARIO_FIM")
    private LocalTime horarioFim;

    public static DiaAcessoHistorico criaDiaAcessoHistorico(DiaAcesso request, HorarioAcessoHistorico historico) {
        return DiaAcessoHistorico.builder()
                .horarioAcessoHistorico(historico)
                .diaSemana(request.getDiaSemana())
                .horarioInicio(request.getHorarioInicio())
                .horarioFim(request.getHorarioFim())
                .build();
    }
}
