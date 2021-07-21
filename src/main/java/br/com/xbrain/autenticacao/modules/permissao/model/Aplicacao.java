package br.com.xbrain.autenticacao.modules.permissao.model;

import br.com.xbrain.autenticacao.modules.permissao.enums.CodigoAplicacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "APLICACAO")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Aplicacao {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "SEQ_APLICACAO", sequenceName = "SEQ_APLICACAO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_APLICACAO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Column(name = "NOME", length = 80)
    private String nome;

    @Column(name = "CODIGO", nullable = false, length = 80)
    @Enumerated(EnumType.STRING)
    private CodigoAplicacao codigo;
}
