package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CARGO_FUNC_SUBNIVEL")
public class CargoFuncionalidadeSubNivel {

    @Id
    @GeneratedValue(generator = "SEQ_CARGO_FUNC_SUBNIVEL", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SEQ_CARGO_FUNC_SUBNIVEL", sequenceName = "SEQ_CARGO_FUNC_SUBNIVEL", allocationSize = 1)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_CARGO", foreignKey = @ForeignKey(name = "FK_CARGO"),
        referencedColumnName = "ID")
    private Cargo cargo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_FUNCIONALIDADE", foreignKey = @ForeignKey(name = "FK_FUNCIONALIDADE"),
        referencedColumnName = "ID", nullable = false)
    private Funcionalidade funcionalidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_SUBNIVEL", foreignKey = @ForeignKey(name = "FK_SUBNIVEL"),
        referencedColumnName = "ID", nullable = false)
    private SubNivel subNivel;
}
