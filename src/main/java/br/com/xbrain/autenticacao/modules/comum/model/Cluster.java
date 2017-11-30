package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EAreaAtuacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "CLUSTERS")
@Data
public class Cluster implements AreaAtuacao {

    @Id
    @Column(name = "ID")
    @GenericGenerator(
            name = "SEQ_CLUSTERS",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_CLUSTERS")})
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_CLUSTERS")
    private Integer id;

    @NotNull
    @Column(name = "NOME", length = 120)
    private String nome;

    @NotNull
    @JoinColumn(name = "FK_GRUPO", referencedColumnName = "ID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_CLUSTER_GRUPO"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Grupo grupo;

    @Column(name = "SITUACAO", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    @JsonIgnore
    public EAreaAtuacao getTipo() {
        return EAreaAtuacao.CLUSTER;
    }

    public Cluster() {
    }

    public Cluster(Integer id) {
        this.id = id;
    }

    public Cluster(Integer id, String nome, Grupo grupo, ESituacao situacao) {
        this.id = id;
        this.nome = nome;
        this.grupo = grupo;
        this.situacao = situacao;
    }
}