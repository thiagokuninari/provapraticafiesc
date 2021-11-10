package br.com.xbrain.autenticacao.modules.horarioacesso.model;

import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoDiaDto;
import br.com.xbrain.autenticacao.modules.horarioacesso.enums.EDiaSemana;
import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioAcessoDia {

    @JoinColumn(name = "FK_HORARIO_ACESSO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(foreignKeyDefinition = "FK_DIA_HORARIO_ACESSO"))
    @ManyToOne(fetch = FetchType.LAZY)
    private HorarioAcesso horarioAcesso;

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
