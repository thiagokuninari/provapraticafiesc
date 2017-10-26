package br.com.xbrain.autenticacao.modules.permissao.model;

import br.com.xbrain.autenticacao.modules.permissao.enums.CodigoAplicacao;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "APLICACAO")
@Data
public class Aplicacao {

    @Id
    @Column(name = "ID")
    @GenericGenerator(
            name = "SEQ_APLICACAO",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_APLICACAO")})
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_APLICACAO")
    private Integer id;

    @NotNull
    @Column(name = "NOME",length = 80)
    private String nome;

    @Column(name = "CODIGO", nullable = false, length = 80)
    @Enumerated(EnumType.STRING)
    private CodigoAplicacao codigo;
}
