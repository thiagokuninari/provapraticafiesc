package br.com.xbrain.autenticacao.modules.usuario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIO_HIERARQUIA")
@Data
public class UsuarioHierarquia {

    @EmbeddedId
    private UsuarioCidadePk usuarioCidadePk;

    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO", foreignKey = @ForeignKey(name = "FK_USUARIO_HIERARQUIA_USUARIO"),
            referencedColumnName = "ID", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO_SUPERIOR", foreignKey = @ForeignKey(name = "FK_USU_HIERARQUIA_USU_SUPERIOR"),
            referencedColumnName = "ID", nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuarioSuperior;

    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO_CADASTRO", foreignKey = @ForeignKey(name = "FK_USU_HIERARQUIA_USU_CAD"),
            referencedColumnName = "ID", nullable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuarioCadastro;

    @NotNull
    @Column(name = "DATA_CADASTRO", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    public UsuarioHierarquia() {
    }

    public UsuarioHierarquia(Usuario usuarioSuperior) {
        this.usuarioSuperior = usuarioSuperior;
    }

    public UsuarioHierarquia(Usuario usuario, Usuario usuarioSuperior, Usuario usuarioCadastro,
                             LocalDateTime dataCadastro) {
        this.usuario = usuario;
        this.usuarioSuperior = usuarioSuperior;
        this.usuarioCadastro = usuarioCadastro;
        this.dataCadastro = dataCadastro;
    }

    public Integer getUsuarioSuperiorId() {
        return usuarioSuperior != null ? usuarioSuperior.getId() : null;
    }

    public void setUsuarioSuperiorId(int id) {
        this.usuarioSuperior = new Usuario(id);
    }

    public String getUsuarioSuperiorNome() {
        return usuarioSuperior != null ? usuarioSuperior.getNome() : null;
    }

    public void setUsuarioSuperiorNome(String nome) {
    }
}