package br.com.xbrain.autenticacao.modules.permissao.model;

import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "FUNCIONALIDADE_CANAL")
@Data
@EqualsAndHashCode(of = "funcionalidadeCanalPk")
@ToString(of = "funcionalidadeCanalPk")
public class FuncionalidadeCanal {

    @EmbeddedId
    private FuncionalidadeCanalPk funcionalidadeCanalPk;

    @JoinColumn(name = "FK_FUNCIONALIDADE", foreignKey = @ForeignKey(name = "FK_FUNCIONALIDADE_CANAL_FUNC"),
            referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Funcionalidade funcionalidade;

    @Column(name = "CANAL", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private ECanal canal;
}
