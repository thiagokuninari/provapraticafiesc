package br.com.xbrain.autenticacao.modules.comum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "UF")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    public Uf(Integer id) {
        this.id = id;
    }
}
