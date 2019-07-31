package br.com.xbrain.autenticacao.modules.usuario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIO_HIERARQUIA")
@Builder
@AllArgsConstructor
@Data
@EqualsAndHashCode(of = "usuarioHierarquiaPk")
@ToString(of = "usuarioHierarquiaPk")
public class UsuarioHierarquia {

    @EmbeddedId
    private UsuarioHierarquiaPk usuarioHierarquiaPk;

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

    private UsuarioHierarquia(Usuario usuario, Integer idHierarquia, Integer idUsuarioAutenticado) {
        this.usuarioHierarquiaPk = new UsuarioHierarquiaPk(usuario.getId(), idHierarquia);
        this.usuario = usuario;
        this.usuarioSuperior = new Usuario(idHierarquia);
        this.usuarioCadastro = new Usuario(idUsuarioAutenticado);
        this.dataCadastro = LocalDateTime.now();
    }

    public static UsuarioHierarquia criar(Usuario usuario, Integer idHierarquia, Integer idUsuarioAutenticado) {
        return new UsuarioHierarquia(usuario, idHierarquia, idUsuarioAutenticado);
    }

    public Integer getUsuarioSuperiorId() {
        return usuarioSuperior != null ? usuarioSuperior.getId() : null;
    }

    public boolean isSuperior(Integer cargoId) {
        return !ObjectUtils.isEmpty(getUsuario().getCargosSuperioresId())
                && getUsuario().getCargosSuperioresId().contains(cargoId);
    }
}