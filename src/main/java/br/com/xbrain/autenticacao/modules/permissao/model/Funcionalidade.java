package br.com.xbrain.autenticacao.modules.permissao.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "FUNCIONALIDADE")
@Data
@EqualsAndHashCode(of = {"id"})
public class Funcionalidade {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "SEQ_FUNCIONALIDADE", sequenceName = "SEQ_FUNCIONALIDADE")
    @GeneratedValue(generator = "SEQ_FUNCIONALIDADE", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Column(name = "NOME")
    private String nome;

    @NotNull
    @Column(name = "ROLE")
    private String role;

    @NotNull
    @JoinColumn(name = "FK_APLICACAO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_FUNCIONALIDADE_APLICACAO"), nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Aplicacao aplicacao;

    @Transient
    private boolean especial;

    public Funcionalidade() { }

    public Funcionalidade(Integer id) {
        this.id = id;
    }

    public Funcionalidade(Integer id, String nome, String role) {
        this.id = id;
        this.nome = nome;
        this.role = role;
    }
}
