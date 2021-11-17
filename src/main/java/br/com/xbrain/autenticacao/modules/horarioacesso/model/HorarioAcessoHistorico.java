package br.com.xbrain.autenticacao.modules.horarioacesso.model;

import lombok.*;

import java.time.LocalDateTime;
import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HORARIO_ACESSO_HIST")
public class HorarioAcessoHistorico {

    @Id
    @SequenceGenerator(name = "SEQ_HORARIO_ACESSO_HIST", sequenceName = "SEQ_HORARIO_ACESSO_HIST", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_HORARIO_ACESSO_HIST", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_HORARIO_ACESSO", referencedColumnName = "ID", nullable = false)
    private HorarioAcesso horarioAcesso;

    @Column(name = "DATA_ALTERACAO", nullable = false)
    private LocalDateTime dataAlteracao;

    @Column(name = "USUARIO_ALTERACAO_ID", nullable = false)
    private Integer usuarioAlteracaoId;

    @Column(name = "USUARIO_ALTERACAO_NOME", nullable = false, length = 100)
    private String usuarioAlteracaoNome;

    public static HorarioAcessoHistorico criaNovoHistorico(HorarioAcesso request) {
        return HorarioAcessoHistorico.builder()
                .horarioAcesso(request)
                .dataAlteracao(request.getDataAlteracao())
                .usuarioAlteracaoId(request.getUsuarioAlteracaoId())
                .usuarioAlteracaoNome(request.getUsuarioAlteracaoNome())
                .build();
    }
}
