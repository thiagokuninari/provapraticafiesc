package br.com.xbrain.autenticacao.modules.horarioacesso.model;

import java.time.LocalTime;

import javax.persistence.*;

import br.com.xbrain.autenticacao.modules.comum.enums.EDiaSemana;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DIA_ACESSO_HIST")
public class DiaAcessoHistorico {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_HORARIO_ACESSO_HIST", referencedColumnName = "ID", nullable = false)
    private HorarioAcessoHistorico horarioAcessoHistorico;

    @Column(name = "DIA_SEMANA")
    @Enumerated(EnumType.STRING)
    private EDiaSemana diaSemana;

    @Column(name = "HORARIO_INICIO")
    private LocalTime horarioInicio;

    @Column(name = "HORARIO_FIM")
    private LocalTime horarioFim;

    public static DiaAcessoHistorico criaDiaAcessoHistorico(DiaAcesso request) {
        return DiaAcessoHistorico.builder()
                .diaSemana(request.getDiaSemana())
                .horarioInicio(request.getHorarioInicio())
                .horarioFim(request.getHorarioFim())
                .build();
    }
}
