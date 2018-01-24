package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "EMPRESA")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = {"id"})
public class Empresa implements Serializable {

    @Id
    @Column(name = "ID")
    @GenericGenerator(
            name = "SEQ_EMPRESA",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_EMPRESA")})
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_EMPRESA")
    private Integer id;

    @NotNull
    @Column(name = "NOME", length = 80, nullable = false)
    private String nome;

    @JsonIgnore
    @NotNull
    @JoinColumn(name = "FK_MARCA", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Marca marca;

    @Column(name = "CODIGO", length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private CodigoEmpresa codigo;

    @JsonIgnore
    @NotNull
    @JoinColumn(name = "FK_UNIDADE_NEGOCIO", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadeNegocio unidadeNegocio;

    @JsonIgnore
    @Column(name = "SITUACAO", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    public Empresa() {
    }

    public Empresa(Integer id) {
        this.id = id;
    }

    public Empresa(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}