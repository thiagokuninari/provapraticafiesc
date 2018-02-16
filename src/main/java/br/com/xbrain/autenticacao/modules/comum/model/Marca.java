package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoMarca;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "MARCA")
@Data
public class Marca {

    @Id
    @SequenceGenerator(name = "SEQ_MARCA", sequenceName = "SEQ_MARCA", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_MARCA", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Column(name = "NOME", length = 30)
    private String nome;

    @Column(name = "CODIGO", length = 30)
    @Enumerated(EnumType.STRING)
    private CodigoMarca codigo;

    @Column(name = "SITUACAO", length = 1)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    public Marca() {
    }

    public Marca(Integer id) {
        this.id = id;
    }
}