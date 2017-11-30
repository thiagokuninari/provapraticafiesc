package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "UNIDADE_NEGOCIO")
@Data
@EqualsAndHashCode(of = {"id"})
public class UnidadeNegocio {

    @Id
    @Column(name = "ID")
    @GenericGenerator(
            name = "SEQ_UNIDADE_NEGOCIO",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_UNIDADE_NEGOCIO")})
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_UNIDADE_NEGOCIO")
    private Integer id;

    @NotNull
    @Size(min = 3, max = 100)
    @Column(name = "NOME", length = 100, nullable = false)
    private String nome;

    @Column(name = "SITUACAO", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    public UnidadeNegocio() {
    }

    public UnidadeNegocio(Integer id) {
        this.id = id;
    }

    public UnidadeNegocio(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }
}