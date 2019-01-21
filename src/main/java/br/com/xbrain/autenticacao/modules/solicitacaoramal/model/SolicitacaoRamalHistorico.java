package br.com.xbrain.autenticacao.modules.solicitacaoramal.model;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "SOLICITACAO_RAMAL_HISTORICO")
public class SolicitacaoRamalHistorico {

    @Id
    @SequenceGenerator(name = "SEQ_SOLICITACAO_RAMAL_HISTORICO",
                       sequenceName = "SEQ_SOLICITACAO_RAMAL_HISTORICO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SOLICITACAO_RAMAL_HISTORICO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_SOLICITACAO_RAMAL_HISTORICO_USUARIO"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false)
    private ESituacao situacao;

    @Column(name = "DATA_CADASTRO", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "COMENTARIO")
    private String comentario;

    @JoinColumn(name = "FK_SOLICITACAO_RAMAL", referencedColumnName = "ID",
                foreignKey = @ForeignKey(name = "FK_SOLICITACAO_RAMAL_HISTORICO_SOLICITACAO_RAMAL"))
    @ManyToOne(fetch = FetchType.LAZY)
    private SolicitacaoRamal solicitacaoRamal;

    public SolicitacaoRamalHistorico gerarHistorico(SolicitacaoRamal solicitacaoRamal) {
        SolicitacaoRamalHistorico historico = new SolicitacaoRamalHistorico();
        historico.setUsuario(solicitacaoRamal.getUsuario());
        historico.setSituacao(solicitacaoRamal.getSituacao());
        historico.setSolicitacaoRamal(solicitacaoRamal);
        historico.setDataCadastro(LocalDateTime.now());

        return historico;
    }

}
