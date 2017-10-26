package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Table(name = "CARGO")
@Data
public class Cargo {

    @Id
    @Column(name = "ID")
    @GenericGenerator(
            name = "SEQ_CARGO",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_CARGO")})
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_CARGO")
    private Integer id;

    @Column(name = "NOME", length = 80, nullable = false)
    private String nome;

    @JoinColumn(name = "FK_NIVEL", foreignKey = @ForeignKey(name = "FK_CARGO_NIVEL"),
            referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Nivel nivel;

    @Column(name = "CODIGO", length = 80)
    private String codigo;

    @Column(name = "SITUACAO", nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    public Cargo() {
    }

    public Cargo(Integer id) {
        this.id = id;
    }
}
