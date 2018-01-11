package br.com.xbrain.autenticacao.modules.usuario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIO_CIDADE")
@Data
@EqualsAndHashCode(of = "usuarioCidadePk")
@ToString(of = "usuarioCidadePk")
public class UsuarioCidade {

    @EmbeddedId
    private UsuarioCidadePk usuarioCidadePk;

    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO", foreignKey = @ForeignKey(name = "FK_USUARIO_CIDADE_USUARIO"),
            referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @JoinColumn(name = "FK_CIDADE", foreignKey = @ForeignKey(name = "FK_USUARIO_CIDADE"),
            referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Cidade cidade;

    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO_CADASTRO", foreignKey = @ForeignKey(name = "FK_USUARIO_CIDADE_USUARIO_CAD"),
            referencedColumnName = "ID", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuarioCadastro;

    @NotNull
    @Column(name = "DATA_CADASTRO", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "DATA_BAIXA")
    private LocalDateTime dataBaixa;

    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO_BAIXA", foreignKey = @ForeignKey(name = "FK_USU_CIDADE_USU_BAIXA"),
            referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuarioBaixa;

    @Transient
    private boolean baixar;

    public UsuarioCidade() {
    }

    public UsuarioCidade(UsuarioCidadePk usuarioCidadePk, Usuario usuario, Cidade cidade,
                         Usuario usuarioCadastro, LocalDateTime dataCadastro) {
        this.usuarioCidadePk = usuarioCidadePk;
        this.usuario = usuario;
        this.cidade = cidade;
        this.usuarioCadastro = usuarioCadastro;
        this.dataCadastro = dataCadastro;
    }
}