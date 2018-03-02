package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "DEPARTAMENTO")
@Data
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
public class Departamento {

    @Id
    @SequenceGenerator(name = "SEQ_DEPARTAMENTO", sequenceName = "SEQ_DEPARTAMENTO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_DEPARTAMENTO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "NOME", length = 80, nullable = false)
    private String nome;

    @JoinColumn(name = "FK_NIVEL", foreignKey = @ForeignKey(name = "FK_DEP_NIVEL"),
            referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Nivel nivel;

    @Column(name = "CODIGO", length = 80)
    @Enumerated(EnumType.STRING)
    private CodigoDepartamento codigo;

    @Column(name = "SITUACAO", nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    public Departamento() {
    }

    public Departamento(Integer id) {
        this.id = id;
    }

    public void forceLoad() {
        nivel.getId();
    }
}
