package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "NIVEL")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
public class Nivel {

    @Id
    @SequenceGenerator(name = "SEQ_NIVEL", sequenceName = "SEQ_NIVEL", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_NIVEL", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "NOME", length = 80, nullable = false)
    private String nome;

    @Column(name = "CODIGO", length = 80)
    @Enumerated(EnumType.STRING)
    private CodigoNivel codigo;

    @Column(name = "SITUACAO", nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    @Column(name = "EXIBIR_CAD_USUARIO", nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    private Eboolean exibirCadastroUsuario;

    public Nivel(Integer id) {
        this.id = id;
    }

}
