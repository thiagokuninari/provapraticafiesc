package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EAreaAtuacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "SUB_CLUSTER")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @JoinColumn(name = "FK_MARCA", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_SUBCLUSTER_FK_MARCA"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Marca marca;

    public static SubCluster of(Integer id, String nome) {
        return SubCluster
                .builder()
                .id(id)
                .nome(nome)
                .build();
    }

    public String getNomeComMarca() {
        return nome + (ObjectUtils.isEmpty(marca) ? "" : " - " + marca.getNome());
    }

    @JsonIgnore
    public EAreaAtuacao getTipo() {
        return EAreaAtuacao.SUBCLUSTER;
    }
}
