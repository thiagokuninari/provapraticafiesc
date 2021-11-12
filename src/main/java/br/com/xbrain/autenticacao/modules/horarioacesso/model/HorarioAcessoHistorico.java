package br.com.xbrain.autenticacao.modules.horarioacesso.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HORARIO_ACESSO_HISTORICO")
public class HorarioAcessoHistorico {

    @Id
    @SequenceGenerator(name = "SEQ_HORARIO_ACESSO_HIST", sequenceName = "SEQ_HORARIO_ACESSO_HIST", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_HORARIO_ACESSO_HIST", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_HORARIO_ACESSO", referencedColumnName = "ID", nullable = false)
    private HorarioAcesso horarioAcesso;

    @Column(name = "DATA_ULTIMA_ALTERACAO", nullable = false)
    private LocalDateTime dataUltimaAlteracao;

    @Column(name = "USUARIO_ALTERACAO", nullable = false)
    private Usuario usuarioAlteracao;

    @OneToMany(mappedBy = "horario_acesso_historico", fetch = FetchType.LAZY)
    private List<DiaAcessoHistorico> diasAcesso;

    public static HorarioAcessoHistorico criaNovoHistorico(HorarioAcesso request) {
        return HorarioAcessoHistorico.builder()
                .horarioAcesso(request)
                .dataUltimaAlteracao(request.getUltimaAlteracao())
                .usuarioAlteracao(request.getUsuarioAlteracao())
                .build();
    }
}
