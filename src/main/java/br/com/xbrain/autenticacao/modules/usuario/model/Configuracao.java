package br.com.xbrain.autenticacao.modules.usuario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "CONFIGURACAO")
public class Configuracao {

    @Id
    @SequenceGenerator(name = "SEQ_CONFIGURACAO", sequenceName = "SEQ_CONFIGURACAO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CONFIGURACAO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO", foreignKey = @ForeignKey(name = "FK_USUARIO_CONFIGURACAO"),
            referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO_CADASTRO", foreignKey = @ForeignKey(name = "FK_USUARIO_CONFIGURACAO_CAD"),
            referencedColumnName = "ID", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuarioCadastro;

    @NotAudited
    @Column(name = "DATA_CADASTRO", updatable = false, nullable = false)
    private LocalDateTime cadastro;

    @Column(name = "RAMAL", nullable = false)
    private Integer ramal;

}
