package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoMarca;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "MARCA")
@Data
public class Marca {

    @Id
    @Column(name = "ID")
    @GenericGenerator(
            name = "SEQ_MARCA",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_MARCA")})
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_MARCA")
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