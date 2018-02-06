package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Table(name = "NIVEL")
@Data
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
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
    @Enumerated(EnumType.STRING)
    private CodigoNivel codigo;

    @Column(name = "SITUACAO", nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    @Column(name = "EXIBIR_CAD_USUARIO", nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    private Eboolean exibirCadastroUsuario;
}
