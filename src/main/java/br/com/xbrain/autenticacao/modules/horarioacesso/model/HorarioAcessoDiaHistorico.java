package br.com.xbrain.autenticacao.modules.horarioacesso.model;

import java.time.LocalTime;

import javax.persistence.*;

import br.com.xbrain.autenticacao.modules.horarioacesso.enums.EDiaSemana;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HORARIO_ACESSO_DIA_HIST")
public class HorarioAcessoDiaHistorico {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_HORARIO_ACESSO_HIST", referencedColumnName = "ID", nullable = false)
    private HorarioAcessoDiaHistorico horarioAcessoDiaHistorico;

    @Column(name = "DIA_SEMANA")
    private EDiaSemana diaSemana;

    @Column(name = "HORARIO_INICIO")
    private LocalTime horarioInicio;

    @Column(name = "HORARIO_FIM")
    private LocalTime horarioFim;

    public static HorarioAcessoDiaHistorico criaDiaAcessoHistorico(HorarioAcessoDia request) {
        return HorarioAcessoDiaHistorico.builder()
                .diaSemana(request.getDiaSemana())
                .horarioInicio(request.getHorarioInicial())
                .horarioFim(request.getHorarioFinal())
                .build();
    }
}
