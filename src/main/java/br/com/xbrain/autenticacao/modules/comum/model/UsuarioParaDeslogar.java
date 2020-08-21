package br.com.xbrain.autenticacao.modules.comum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USUARIOS_PARA_DESLOGAR")
public class UsuarioParaDeslogar {

    @Id
    @Column(name = "USUARIO_ID")
    private Integer usuarioId;

}
