package br.com.xbrain.autenticacao.modules.permissao.model;

import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Data
@Embeddable
public class FuncionalidadeCanalPk implements Serializable {

    @Column(name = "FK_FUNCIONALIDADE", nullable = false)
    private Integer funcionalidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "CANAL", nullable = false)
    private ECanal canal;
}
