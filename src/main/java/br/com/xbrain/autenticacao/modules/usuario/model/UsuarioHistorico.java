package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Builder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIO_HISTORICO")
@Data
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
public class UsuarioHistorico {

    @Id
    @SequenceGenerator(name = "SEQ_USUARIO_HISTORICO", sequenceName = "SEQ_USUARIO_HISTORICO")
    @GeneratedValue(generator = "SEQ_USUARIO_HISTORICO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_USUARIO_HISTORICO"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_MOTIVO_INATIV", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_USUARIO_HIST_MOT_INATIV"))
    private MotivoInativacao motivoInativacao;

    @NotNull
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_USUARIO_INATIVACAO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_USUARIO_HIST_INATIVACAO"))
    private Usuario usuarioInativacao;

    @NotNull
    @Column(name = "DATA_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    @Size(max = 250)
    @Column(length = 250)
    private String observacao;

    @Column(name = "SITUACAO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    public UsuarioHistorico() {
    }

    @Builder
    public UsuarioHistorico(Usuario usuario, MotivoInativacao motivoInativacao, Usuario usuarioInativacao,
                            LocalDateTime dataCadastro, String observacao, ESituacao situacao) {
        this.usuario = usuario;
        this.motivoInativacao = motivoInativacao;
        this.usuarioInativacao = usuarioInativacao;
        this.dataCadastro = dataCadastro;
        this.observacao = observacao;
        this.situacao = situacao;
    }
}
