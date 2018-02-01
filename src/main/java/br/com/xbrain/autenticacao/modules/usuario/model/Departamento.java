package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Table(name = "DEPARTAMENTO")
@Data
public class Departamento {

    @Id
    @Column(name = "ID")
    @GenericGenerator(
            name = "SEQ_DEPARTAMENTO",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_DEPARTAMENTO")})
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_DEPARTAMENTO")
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
}
