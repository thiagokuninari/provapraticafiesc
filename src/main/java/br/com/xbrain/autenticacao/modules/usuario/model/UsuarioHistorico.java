package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.EObservacaoHistorico;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIO_HISTORICO")
@Data
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioHistorico {

    private static final Integer ID_MOTIVO_INATIVACAO_EXCESSO_USO = 9;

    @Id
    @SequenceGenerator(name = "SEQ_USUARIO_HISTORICO", sequenceName = "SEQ_USUARIO_HISTORICO", allocationSize = 1)
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
    @JoinColumn(name = "FK_USUARIO_ALTERACAO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_USUARIO_HIST_ALTERACAO"))
    private Usuario usuarioAlteracao;

    @NotNull
    @Column(name = "DATA_CADASTRO", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Size(max = 250)
    @Column(length = 250)
    private String observacao;

    @Column(name = "SITUACAO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    @JoinColumn(name = "FK_USUARIO_FERIAS", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_USUARIO_FERIAS_USU_HIS"))
    @ManyToOne(fetch = FetchType.LAZY)
    private UsuarioFerias ferias;

    @JoinColumn(name = "FK_USUARIO_AFASTAMENTO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_USUARIO_AFASTAMENTO_USU_HIS"))
    @ManyToOne(fetch = FetchType.LAZY)
    private UsuarioAfastamento afastamento;

    public UsuarioHistorico(Usuario usuario, MotivoInativacao motivoInativacao, Usuario usuarioAlteracao,
                            LocalDateTime dataCadastro, String observacao, ESituacao situacao) {
        this.usuario = usuario;
        this.motivoInativacao = motivoInativacao;
        this.usuarioAlteracao = usuarioAlteracao;
        this.dataCadastro = dataCadastro;
        this.observacao = observacao;
        this.situacao = situacao;
    }

    public static UsuarioHistorico gerarHistorico(Integer usuarioId, MotivoInativacao motivo,
                                                  String observacao, ESituacao situacao) {
        Usuario usuario = new Usuario(usuarioId);
        return new UsuarioHistorico(usuario, motivo, usuario, LocalDateTime.now(), observacao, situacao);
    }

    public static UsuarioHistorico gerarHistorico(Usuario usuario, EObservacaoHistorico observacao) {
        return UsuarioHistorico.builder()
            .dataCadastro(LocalDateTime.now())
            .usuario(usuario)
            .observacao(observacao.getObservacao())
            .situacao(usuario.getSituacao())
            .build();
    }

    public static UsuarioHistorico criarHistoricoAtivacao(Usuario usuarioAlteracao,
                                                          String observacao,
                                                          Usuario usuarioAtivado) {
        return UsuarioHistorico.builder()
                .dataCadastro(LocalDateTime.now())
                .usuario(usuarioAtivado)
                .usuarioAlteracao(usuarioAlteracao)
                .observacao(observacao)
                .situacao(ESituacao.A)
                .build();
    }

    public String getSituacaoComMotivo() {
        return situacao.getDescricao().toUpperCase()
                + (!ObjectUtils.isEmpty(motivoInativacao)
                        ? " / " +  motivoInativacao.getDescricao()
                        : "");
    }

    public static UsuarioHistorico gerarHistoricoDeBloqueioPorExcessoDeUso(Usuario usuario,
                                                                           MotivoInativacao motivoInativacao) {
        return UsuarioHistorico
            .builder()
            .situacao(ESituacao.I)
            .observacao("Inativado pelo timer de usuários por excesso de uso da API.")
            .motivoInativacao(motivoInativacao)
            .dataCadastro(LocalDateTime.now())
            .usuario(usuario)
            .build();
    }
}
