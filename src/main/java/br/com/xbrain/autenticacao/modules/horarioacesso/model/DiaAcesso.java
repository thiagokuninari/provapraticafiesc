package br.com.xbrain.autenticacao.modules.horarioacesso.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EDiaSemana;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.DiaAcessoResponse;
import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DIA_ACESSO")
public class DiaAcesso {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_HORARIO_ACESSO", referencedColumnName = "ID", nullable = false)
    private HorarioAcesso horarioAcesso;

    @Column(name = "DIA_SEMANA")
    @Enumerated(EnumType.STRING)
    private EDiaSemana diaSemana;

    @Column(name = "HORARIO_INICIO")
    private LocalTime horarioInicio;

    @Column(name = "HORARIO_FIM")
    private LocalTime horarioFim;

    public static DiaAcesso converFrom(DiaAcessoResponse request) {
        return DiaAcesso.builder()
            .horarioAcesso(new HorarioAcesso(request.getHorarioAcessoId()))
            .diaSemana(EDiaSemana.valueOf(request.getDiaSemana()))
            .horarioInicio(request.getHorarioInicio())
            .horarioFim(request.getHorarioFim())
            .build();
    }
}
