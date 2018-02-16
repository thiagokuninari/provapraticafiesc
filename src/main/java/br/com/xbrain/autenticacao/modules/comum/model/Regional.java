package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EAreaAtuacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "REGIONAL")
@Data
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
    public EAreaAtuacao getTipo() {
        return EAreaAtuacao.REGIONAL;
    }

    public Regional() {
    }

    public Regional(Integer id) {
        this.id = id;
    }

    public Regional(Integer id, String nome, ESituacao situacao) {
        this.id = id;
        this.nome = nome;
        this.situacao = situacao;
    }
}