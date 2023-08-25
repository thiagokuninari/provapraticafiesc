package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "SUB_CANAL")
@Data
@ToString(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubCanal {

    @Id
    @SequenceGenerator(name = "SEQ_SUB_CANAL", sequenceName = "SEQ_SUBCANAL", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SUB_CANAL", strategy = GenerationType.SEQUENCE)
    private Integer id;
    @Column(name = "CODIGO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ETipoCanal codigo;
    @Column(name = "NOME", nullable = false)
    private String nome;
    @Column(name = "SITUACAO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    public SubCanal(Integer id) {
        this.id = id;
    }
}
