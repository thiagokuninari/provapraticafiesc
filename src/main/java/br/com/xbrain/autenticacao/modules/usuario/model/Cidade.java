package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "CIDADE")
@Data
@EqualsAndHashCode(of = {"id"})
public class Cidade {

    @Id
    @Column(name = "ID")
    @GenericGenerator(
            name = "SEQ_CIDADE",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_CIDADE")})
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_CIDADE")
    private Integer id;

    @NotNull
    @Column(name = "NOME", length = 180)
    private String nome;

    @Column(name = "CODIGO_IBGE", length = 15)
    private String codigoIbge;

    @NotNull
    @JoinColumn(name = "FK_UF", referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Uf uf;

    @JoinColumn(name = "FK_SUB_CLUSTER", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_CIDADE_SUB_CLUSTER"))
    @ManyToOne(fetch = FetchType.LAZY)
    private SubCluster subCluster;

    @JoinColumn(name = "FK_USUARIO_APROVADOR_MSO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_CIDADE_USU_MSO"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuarioAprovadorMso;

    @JsonIgnore
    @OneToMany(mappedBy = "cidade", fetch = FetchType.LAZY)
    private List<UsuarioCidade> cidadeUsuarios;

    public Cidade() {
    }

    public Cidade(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    public String getNomeComUf() {
        return uf != null ? nome + " - " + uf.getUf() : nome;
    }

    @JsonIgnore
    public String getRegionalNome() {
        return subCluster.getCluster().getGrupo().getRegional().getNome();
    }

    @JsonIgnore
    public String getGrupoNome() {
        return subCluster.getCluster().getGrupo().getNome();
    }

    @JsonIgnore
    public String getClusterNome() {
        return subCluster.getCluster().getNome();
    }

    @JsonIgnore
    public String getSubClusterNome() {
        return subCluster.getNome();
    }
}