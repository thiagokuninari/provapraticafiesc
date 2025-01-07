package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "CIDADE")
@Data
@EqualsAndHashCode(of = {"id"})
@ToString(of = "id")
@AllArgsConstructor
@Builder
public class Cidade {

    @Id
    @SequenceGenerator(name = "SEQ_CIDADE", sequenceName = "SEQ_CIDADE", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CIDADE", strategy = GenerationType.SEQUENCE)
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

    @Column(name = "NET_UNO")
    @Enumerated(EnumType.STRING)
    private Eboolean netUno;

    @Column(name = "FK_CIDADE")
    private Integer fkCidade;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cidade", fetch = FetchType.LAZY)
    private List<CidadeDbm> cidadesDbm;

    @JoinColumn(name = "FK_REGIONAL", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Regional regional;

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
    public Integer getRegionalId() {
        return Objects.nonNull(this.getRegional())
            ? this.getRegional().getId()
            : null;
    }

    @JsonIgnore
    public String getRegionalNome() {
        return Objects.nonNull(this.getRegional())
            ? this.getRegional().getNome()
            : null;
    }

    @JsonIgnore
    public Integer getGrupoId() {
        return subCluster.getCluster().getGrupo().getId();
    }

    @JsonIgnore
    public String getGrupoNome() {
        return subCluster.getCluster().getGrupo().getNome();
    }

    @JsonIgnore
    public Integer getClusterId() {
        return subCluster.getCluster().getId();
    }

    @JsonIgnore
    public String getClusterNome() {
        return subCluster.getCluster().getNome();
    }

    @JsonIgnore
    public Integer getSubClusterId() {
        return subCluster.getId();
    }

    @JsonIgnore
    public String getSubClusterNome() {
        return subCluster.getNomeComMarca();
    }

    @JsonIgnore
    public Integer getIdUf() {
        return ObjectUtils.isEmpty(uf) ? null : uf.getId();
    }

    @JsonIgnore
    public String getNomeUf() {
        return ObjectUtils.isEmpty(uf) ? null : uf.getNome();
    }

    @JsonIgnore
    public String getCodigoUf() {
        return ObjectUtils.isEmpty(uf) ? null : uf.getUf();
    }

    public static Set<Cidade> of(List<Integer> cidades) {
        return cidades.stream()
                .map(Cidade::new)
                .collect(Collectors.toSet());
    }

    public static Set<Integer> convertFrom(Set<Cidade> cidades) {
        return cidades.stream()
                .map(Cidade::getId)
                .collect(Collectors.toSet());
    }
}
