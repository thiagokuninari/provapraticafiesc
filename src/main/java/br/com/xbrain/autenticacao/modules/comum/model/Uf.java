package br.com.xbrain.autenticacao.modules.comum.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "UF")
@Data
public class Uf {

    @Id
    @Column(name = "ID")
    @GenericGenerator(
            name = "SEQ_UF",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_UF")})
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_UF")
    private Integer id;

    @NotNull
    @Column(name = "NOME")
    private String nome;

    @NotNull
    @Column(name = "UF")
    private String uf;

    public Uf() {
    }

    public Uf(Integer id) {
        this.id = id;
    }
}