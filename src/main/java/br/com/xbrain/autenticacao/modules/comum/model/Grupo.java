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
@Table(name = "GRUPO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Grupo implements AreaAtuacao {

    @Id
    @SequenceGenerator(name = "SEQ_GRUPO", sequenceName = "SEQ_GRUPO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_GRUPO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Column(name = "NOME", length = 100, nullable = false)
    private String nome;

    @NotNull
    @JoinColumn(name = "FK_REGIONAL", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_GRUPO_REGIONAL"), nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Regional regional;

    @Column(name = "SITUACAO", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    public Grupo(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    public EAreaAtuacao getTipo() {
        return EAreaAtuacao.GRUPO;
    }
}
