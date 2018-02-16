package br.com.xbrain.autenticacao.modules.comum.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "UF")
@Data
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

    public Uf() {
    }

    public Uf(Integer id) {
        this.id = id;
    }
}