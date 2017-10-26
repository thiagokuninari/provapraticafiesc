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
    @GenericGenerator(
            name = "SEQ_FUNCIONALIDADE",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_FUNCIONALIDADE")})
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_FUNCIONALIDADE")
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
}
