package br.com.xbrain.autenticacao.modules.usuario.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CIDADE_DBM")
public class CidadeDbm {

    @Id
    @SequenceGenerator(name = "SEQ_CIDADE_DBM", sequenceName = "SEQ_CIDADE_DBM", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CIDADE_DBM", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @JoinColumn(name = "FK_CIDADE", referencedColumnName = "ID", foreignKey = @ForeignKey(name = "FK_CIDADE_DBM"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Cidade cidade;

    @Column(name = "CODIGO_CIDADE_DBM")
    private Integer codigoCidadeDbm;
}
