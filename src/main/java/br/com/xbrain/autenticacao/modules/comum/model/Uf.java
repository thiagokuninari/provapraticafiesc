package br.com.xbrain.autenticacao.modules.comum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "UF")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Uf {

    @Id
    @SequenceGenerator(name = "SEQ_UF", sequenceName = "SEQ_UF", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_UF", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Column(name = "NOME")
    private String nome;

    @NotNull
    @Column(name = "UF")
    private String uf;

    @NotAudited
    @JsonIgnore
    @NotEmpty
    @JoinTable(name = "UF_REGIONAL", joinColumns = {
        @JoinColumn(name = "FK_UF", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_UF_REGIONAL"))}, inverseJoinColumns = {
        @JoinColumn(name = "FK_REGIONAL", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_UF_REGIONAL"))})
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Regional> regionais;

    public Uf(Integer id) {
        this.id = id;
    }

    public static Set<Uf> of(List<Integer> estadosIds) {
        return estadosIds.stream()
            .map(Uf::new)
            .collect(Collectors.toSet());
    }

    public static Set<Integer> convertFrom(Set<Uf> estadosIds) {
        return estadosIds.stream()
            .map(Uf::getId)
            .collect(Collectors.toSet());
    }
}
