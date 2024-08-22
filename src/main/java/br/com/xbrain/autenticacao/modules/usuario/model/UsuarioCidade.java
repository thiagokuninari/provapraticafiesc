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

    private UsuarioCidade(Usuario usuario, Integer idCidade, Integer idUsuarioLogado) {
        this.usuarioCidadePk = new UsuarioCidadePk(usuario.getId(), idCidade);
        this.usuario = usuario;
        this.cidade = new Cidade(idCidade);
        this.usuarioCadastro = new Usuario(idUsuarioLogado);
        this.dataCadastro = LocalDateTime.now();
    }

    public static UsuarioCidade criar(Usuario usuario, Integer idCidade, Integer idUsuarioLogado) {
        return new UsuarioCidade(usuario, idCidade, idUsuarioLogado);
    }
}
