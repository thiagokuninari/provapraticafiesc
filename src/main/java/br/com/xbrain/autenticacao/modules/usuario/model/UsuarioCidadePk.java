package br.com.xbrain.autenticacao.modules.usuario.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@EqualsAndHashCode(of = {"usuario", "cidade"})
@ToString(of = {"usuario", "cidade"})
public class UsuarioCidadePk implements Serializable {

    @Column(name = "FK_USUARIO", nullable = false)
    private Integer usuario;

    @Column(name = "FK_CIDADE", nullable = false)
    private Integer cidade;

    public UsuarioCidadePk() {
    }

    public UsuarioCidadePk(Integer usuario, Integer cidade) {
        this.usuario = usuario;
        this.cidade = cidade;
    }
}
