package br.com.xbrain.autenticacao.modules.horarioacesso.model;

import java.time.LocalTime;

import javax.persistence.*;

import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAtuacaoDto;
import br.com.xbrain.autenticacao.modules.horarioacesso.enums.EDiaSemana;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HORARIO_ATUACAO")
public class HorarioAtuacao {
    @Id
    @SequenceGenerator(name = "SEQ_HORARIO_ATUACAO", sequenceName = "SEQ_HORARIO_ATUACAO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_HORARIO_ATUACAO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @JoinColumn(name = "FK_HORARIO_ACESSO", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private HorarioAcesso horarioAcesso;

    @JoinColumn(name = "FK_HORARIO_HISTORICO", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private HorarioHistorico horarioHistorico;

    @Column(name = "DIA_SEMANA")
    @Enumerated(EnumType.STRING)
    private EDiaSemana diaSemana;

    @Column(name = "HORARIO_INICIO")
    private LocalTime horarioInicio;

    @Column(name = "HORARIO_FIM")
    private LocalTime horarioFim;

    public static HorarioAtuacao of(HorarioAtuacaoDto request) {
        return HorarioAtuacao.builder()
            .id(request.getId())
            .diaSemana(EDiaSemana.valueOf(request.getDiaSemana()))
            .horarioInicio(LocalTime.parse(request.getHorarioInicio()))
            .horarioFim(LocalTime.parse(request.getHorarioFim()))
            .build();
    }
}
