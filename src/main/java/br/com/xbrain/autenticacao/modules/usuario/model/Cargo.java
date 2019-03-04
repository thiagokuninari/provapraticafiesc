package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "CARGO")
@Data
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
public class Cargo {

    @Id
    @SequenceGenerator(name = "SEQ_CARGO", sequenceName = "SEQ_CARGO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CARGO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "NOME", length = 80, nullable = false)
    private String nome;

    @JoinColumn(name = "FK_NIVEL", foreignKey = @ForeignKey(name = "FK_CARGO_NIVEL"),
            referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Nivel nivel;

    @Column(name = "CODIGO", length = 80)
    @Enumerated(EnumType.STRING)
    private CodigoCargo codigo;

    @Column(name = "SITUACAO", nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    @NotNull
    @Column(name = "QUANTIDADE_SUPERIOR", length = 2)
    private Integer quantidadeSuperior;

    @JsonIgnore
    @JoinTable(name = "CARGO_SUPERIOR", joinColumns = {
            @JoinColumn(name = "FK_CARGO", referencedColumnName = "ID")}, inverseJoinColumns = {
            @JoinColumn(name = "FK_CARGO_SUPERIOR", referencedColumnName = "ID")})
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Cargo> superiores;

    public Cargo() {
    }

    public Cargo(Integer id) {
        this.id = id;
    }

    public Set<Integer> getCargosSuperioresId() {
        return !ObjectUtils.isEmpty(getSuperiores())
                ? getSuperiores().stream().map(Cargo::getId).collect(Collectors.toSet())
                : null;
    }
}
