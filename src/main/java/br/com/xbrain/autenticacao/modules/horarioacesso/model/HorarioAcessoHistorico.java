package br.com.xbrain.autenticacao.modules.horarioacesso.model;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
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

    @Column(name = "USUARIO_ALTERACAO", nullable = false)
    private Usuario usuarioAlteracao;

    public static HorarioAcessoHistorico criaNovoHistorico(HorarioAcesso request) {
        return HorarioAcessoHistorico.builder()
                .horarioAcesso(request)
                .dataAlteracao(request.getDataAlteracao())
                .usuarioAlteracao(request.getUsuarioAlteracao())
                .build();
    }
}
