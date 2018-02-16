package br.com.xbrain.autenticacao.modules.permissao.model;

import br.com.xbrain.autenticacao.modules.permissao.enums.CodigoAplicacao;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "APLICACAO")
@Data
public class Aplicacao {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "SEQ_APLICACAO", sequenceName = "SEQ_APLICACAO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_APLICACAO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Column(name = "NOME",length = 80)
    private String nome;

    @Column(name = "CODIGO", nullable = false, length = 80)
    @Enumerated(EnumType.STRING)
    private CodigoAplicacao codigo;
}
