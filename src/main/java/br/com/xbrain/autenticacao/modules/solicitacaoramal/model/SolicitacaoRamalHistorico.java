package br.com.xbrain.autenticacao.modules.solicitacaoramal.model;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SOLICITACAO_RAMAL_HISTORICO")
public class SolicitacaoRamalHistorico {

    @Id
    @SequenceGenerator(name = "SEQ_SOLIC_RM_HIST",
            sequenceName = "SEQ_SOLIC_RM_HIST", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SOLIC_RM_HIST", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_SOLIC_RM_HIST_USUARIO"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false)
    private ESituacaoSolicitacao situacao;

    @Column(name = "DATA_CADASTRO", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "COMENTARIO")
    private String comentario;

    @JoinColumn(name = "FK_SOLICITACAO_RAMAL", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_SOLIC_RM_HIST_SOLIC_RAMAL"))
    @ManyToOne(fetch = FetchType.LAZY)
    private SolicitacaoRamal solicitacaoRamal;

    public SolicitacaoRamalHistorico gerarHistorico(SolicitacaoRamal solicitacaoRamal, String comentario) {
        SolicitacaoRamalHistorico historico = new SolicitacaoRamalHistorico();
        historico.setUsuario(solicitacaoRamal.getUsuario());
        historico.setSituacao(solicitacaoRamal.getSituacao());
        historico.setSolicitacaoRamal(solicitacaoRamal);
        historico.setDataCadastro(LocalDateTime.now());
        historico.setComentario(comentario);

        return historico;
    }

}
