package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "UNIDADE_NEGOCIO")
@Data
@EqualsAndHashCode(of = {"id"})
@ToString(of = "id")
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnidadeNegocio {

    @Id
    @SequenceGenerator(name = "SEQ_UNIDADE_NEGOCIO", sequenceName = "SEQ_UNIDADE_NEGOCIO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_UNIDADE_NEGOCIO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Size(min = 3, max = 100)
    @Column(name = "NOME", length = 100, nullable = false)
    private String nome;

    @NotNull
    @Column(name = "CODIGO", length = 80)
    @Enumerated(EnumType.STRING)
    private CodigoUnidadeNegocio codigo;

    @Column(name = "SITUACAO", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    public UnidadeNegocio(Integer id) {
        this.id = id;
    }

    public UnidadeNegocio(Integer id, String nome, CodigoUnidadeNegocio codigo) {
        this.id = id;
        this.nome = nome;
        this.codigo = codigo;
    }
}
