package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "ORGANIZACAO")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Organizacao {

    @Id
    @SequenceGenerator(name = "SEQ_ORGANIZACAO", sequenceName = "SEQ_ORGANIZACAO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_ORGANIZACAO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Column(name = "NOME", length = 80, nullable = false)
    private String nome;

    @NotNull
    @Column(name = "CODIGO", length = 80, nullable = false, unique = true)
    private String codigo;

    @JoinTable(name = "ORGANIZACAO_NIVEL", joinColumns = {
            @JoinColumn(name = "FK_NIVEL", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "FK_ORG_NIVEL_NIVEL"))}, inverseJoinColumns = {
            @JoinColumn(name = "FK_ORGANIZACAO", referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "FK_ORG_NIVEL_ORGANIZACAO"))})
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Nivel> niveis;

    public Organizacao(Integer id) {
        this.id = id;
    }
}
