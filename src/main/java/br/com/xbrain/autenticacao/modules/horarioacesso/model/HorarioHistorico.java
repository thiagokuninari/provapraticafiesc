package br.com.xbrain.autenticacao.modules.horarioacesso.model;

import lombok.*;

import java.time.LocalDateTime;
import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HORARIO_HISTORICO")
public class HorarioHistorico {
    @Id
    @SequenceGenerator(name = "SEQ_HORARIO_HISTORICO", sequenceName = "SEQ_HORARIO_HISTORICO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_HORARIO_HISTORICO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_HORARIO_ACESSO", referencedColumnName = "ID", nullable = false)
    private HorarioAcesso horarioAcesso;

    @Column(name = "DATA_ALTERACAO")
    private LocalDateTime dataAlteracao;

    @Column(name = "USUARIO_ALTERACAO_ID")
    private Integer usuarioAlteracaoId;

    @Column(name = "USUARIO_ALTERACAO_NOME")
    private String usuarioAlteracaoNome;

    public static HorarioHistorico of(HorarioAcesso horarioAcesso) {
        return HorarioHistorico.builder()
            .horarioAcesso(horarioAcesso)
            .dataAlteracao(horarioAcesso.getDataAlteracao())
            .usuarioAlteracaoId(horarioAcesso.getUsuarioAlteracaoId())
            .usuarioAlteracaoNome(horarioAcesso.getUsuarioAlteracaoNome())
            .build();
    }
}