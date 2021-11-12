package br.com.xbrain.autenticacao.modules.horarioacesso.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EDiaSemana;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoDiaDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HORARIO_ACESSO_DIA")
public class HorarioAcessoDia {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_HORARIO_ACESSO", referencedColumnName = "ID", nullable = false)
    private HorarioAcesso horarioAcesso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_HORARIO_ACESSO_HIST", referencedColumnName = "ID")
    private HorarioAcesso horarioAcessoHistorico;

    @Column(name = "DIA_SEMANA")
    private EDiaSemana diaSemana;

    @Column(name = "HORARIO_INICIAL")
    private LocalTime horarioInicial;

    @Column(name = "HORARIO_FINAL")
    private LocalTime horarioFinal;

    public static HorarioAcessoDia converFrom(HorarioAcessoDiaDto request) {
        return HorarioAcessoDia.builder()
            .diaSemana(EDiaSemana.valueOf(request.getDiaSemana()))
            .build();
    }
}
