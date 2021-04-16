package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EAreaAtuacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "CLUSTERS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cluster implements AreaAtuacao {

    @Id
    @SequenceGenerator(name = "SEQ_CLUSTERS", sequenceName = "SEQ_CLUSTERS", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CLUSTERS", strategy = GenerationType.SEQUENCE)
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

    public Cluster(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    public EAreaAtuacao getTipo() {
        return EAreaAtuacao.CLUSTER;
    }
}
