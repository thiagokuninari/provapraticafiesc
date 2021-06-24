package br.com.xbrain.autenticacao.modules.usuario.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Builder
@Embeddable
@EqualsAndHashCode(of = {"usuario", "usuarioSuperior"})
@ToString(of = {"usuario", "usuarioSuperior"})
@NoArgsConstructor
public class UsuarioHierarquiaPk implements Serializable {

    @Column(name = "FK_USUARIO", nullable = false)
    private Integer usuario;

    @Column(name = "FK_USUARIO_SUPERIOR", nullable = false)
    private Integer usuarioSuperior;

    public UsuarioHierarquiaPk(Integer usuario, Integer usuarioSuperior) {
        this.usuario = usuario;
        this.usuarioSuperior = usuarioSuperior;
    }
}
