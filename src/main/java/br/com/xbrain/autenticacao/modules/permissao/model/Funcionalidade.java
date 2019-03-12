package br.com.xbrain.autenticacao.modules.permissao.model;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
@Table(name = "FUNCIONALIDADE")
@Data
@EqualsAndHashCode(of = {"id"})
public class Funcionalidade {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "SEQ_FUNCIONALIDADE", sequenceName = "SEQ_FUNCIONALIDADE", allocationSize = 1)
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

    @Column(name = "PERMISSAO_TELA", length = 1)
    @Enumerated(EnumType.STRING)
    private Eboolean permissaoTela;

    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "FUNCIONALIDADE_CANAL",
            joinColumns = @JoinColumn(name = "FK_FUNCIONALIDADE"))
    @Column(name = "CANAL", nullable = false)
    @Enumerated(EnumType.STRING)
    private Collection<ECanal> canais;

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

    public Funcionalidade(Integer id, String nome, String role, Eboolean permissaoTela) {
        this.id = id;
        this.nome = nome;
        this.role = role;
        this.permissaoTela = permissaoTela;
    }
}
