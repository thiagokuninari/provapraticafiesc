package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.DIRETOR_OPERACAO;

@Entity
@Table(name = "CARGO")
@Data
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @CollectionTable(
        name = "CARGO_CANAL",
        joinColumns = @JoinColumn(
            name = "FK_CARGO",
            foreignKey = @ForeignKey(name = "FK_CARGO_CANAL"),
            referencedColumnName = "ID"
        )
    )
    @Column(name = "CANAL", nullable = false, length = 50)
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<ECanal> canais;

    @JsonIgnore
    @JoinTable(name = "CARGO_SUPERIOR", joinColumns = {
            @JoinColumn(name = "FK_CARGO", referencedColumnName = "ID")}, inverseJoinColumns = {
            @JoinColumn(name = "FK_CARGO_SUPERIOR", referencedColumnName = "ID")})
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Cargo> superiores;

    public Cargo(Integer id) {
        this.id = id;
    }

    public Set<Integer> getCargosSuperioresId() {
        return !ObjectUtils.isEmpty(getSuperiores())
                ? getSuperiores().stream().map(Cargo::getId).collect(Collectors.toSet())
                : null;
    }

    public boolean hasPermissaoSobreOCanal(ECanal canal) {
        return ObjectUtils.isEmpty(canais) || Objects.isNull(canal) || canais.contains(canal);
    }

    public boolean isDiretorOperacao() {
        return this.codigo == DIRETOR_OPERACAO;
    }
}
