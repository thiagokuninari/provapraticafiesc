package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EAreaAtuacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "REGIONAL")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Regional implements AreaAtuacao {

    @Id
    @SequenceGenerator(name = "SEQ_REGIONAL", sequenceName = "SEQ_REGIONAL", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_REGIONAL", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Column(name = "NOME", length = 100, nullable = false)
    private String nome;

    @Column(name = "SITUACAO", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    @JsonIgnore
    @Column(name = "NOVA_REGIONAL", length = 1)
    @Enumerated(EnumType.STRING)
    private Eboolean novaRegional;

    public Regional(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    public EAreaAtuacao getTipo() {
        return EAreaAtuacao.REGIONAL;
    }
}
