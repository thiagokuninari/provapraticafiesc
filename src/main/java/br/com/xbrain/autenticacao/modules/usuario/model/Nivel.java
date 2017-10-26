package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.permissao.model.Aplicacao;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Table(name = "NIVEL")
@Data
public class Nivel {

    @Id
    @Column(name = "ID")
    @GenericGenerator(
            name = "SEQ_NIVEL",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_NIVEL")})
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_NIVEL")
    private Integer id;

    @Column(name = "NOME", length = 80, nullable = false)
    private String nome;

    @Column(name = "CODIGO", length = 80)
    private String codigo;

    @JoinColumn(name = "FK_APLICACAO", foreignKey = @ForeignKey(name = "FK_APLICACAO"),
            referencedColumnName = "ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Aplicacao aplicacao;

    @Column(name = "SITUACAO", nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;
}
