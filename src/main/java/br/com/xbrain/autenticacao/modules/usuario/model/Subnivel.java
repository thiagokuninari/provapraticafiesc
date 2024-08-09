package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SUB_NIVEL")
public class Subnivel {

    @Id
    @GeneratedValue(generator = "SEQ_SUB_NIVEL", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SEQ_SUB_NIVEL", sequenceName = "SEQ_SUB_NIVEL", allocationSize = 1)
    private Integer id;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "CODIGO")
    private String codigo;

    @Column(name = "SITUACAO")
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_NIVEL", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_NIVEL"))
    private Nivel nivel;

    @NotAudited
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "FUNCIONALIDADE_SUBNIVEL", joinColumns = {
        @JoinColumn(name = "FK_SUBNIVEL", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_SUBNIVEL"))}, inverseJoinColumns = {
        @JoinColumn(name = "FK_FUNCIONALIDADE", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_FUNCIONALIDADE"))})
    private Set<Funcionalidade> funcionalidades;

    public List<Integer> getFuncionalidadesIds() {
        return this.funcionalidades.stream()
            .map(Funcionalidade::getId)
            .collect(Collectors.toList());
    }
}
