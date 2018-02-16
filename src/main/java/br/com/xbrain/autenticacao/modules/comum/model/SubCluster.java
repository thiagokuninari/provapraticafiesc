package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EAreaAtuacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "SUB_CLUSTER")
@Data
public class SubCluster implements AreaAtuacao {

    @Id
    @SequenceGenerator(name = "SEQ_SUB_CLUSTER", sequenceName = "SEQ_SUB_CLUSTER", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SUB_CLUSTER", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Column(name = "NOME", length = 100, nullable = false)
    private String nome;

    @NotNull
    @JoinColumn(name = "FK_CLUSTER", referencedColumnName = "ID", nullable = false,
            foreignKey = @ForeignKey(name = "FK_SUBCLUSTER_CLUSTER"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Cluster cluster;

    @Column(name = "SITUACAO", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    @JsonIgnore
    @OneToMany(mappedBy = "subCluster", fetch = FetchType.LAZY)
    private List<Cidade> cidades;

    public SubCluster() {
    }

    public SubCluster(Integer id) {
        this.id = id;
    }

    public SubCluster(Integer id, String nome, Cluster cluster, ESituacao situacao) {
        this.id = id;
        this.nome = nome;
        this.cluster = cluster;
        this.situacao = situacao;
    }

    @JsonIgnore
    public EAreaAtuacao getTipo() {
        return EAreaAtuacao.SUBCLUSTER;
    }
}
