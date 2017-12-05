package br.com.xbrain.autenticacao.modules.permissao.model;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Entity
@Table(name = "PERMISSAO_ESPECIAL")
@Data
public class PermissaoEspecial {

    @Id
    @Column(name = "ID")
    @GenericGenerator(
            name = "SEQ_PERMISSAO_ESPECIAL",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "SEQ_PERMISSAO_ESPECIAL")})
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_PERMISSAO_ESPECIAL")
    private Integer id;

    @JoinColumn(name = "FK_USUARIO", foreignKey = @ForeignKey(name = "FK_PERMISSAO_ESPECIAL_USU"),
            referencedColumnName = "id", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @JoinColumn(name = "FK_FUNCIONALIDADE", foreignKey = @ForeignKey(name = "FK_PERMISSAO_ESPECIAL_FUNC"),
            referencedColumnName = "id", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Funcionalidade funcionalidade;

    @NotNull
    @Column(name = "DATA_CADASTRO", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "DATA_BAIXA")
    private LocalDateTime dataBaixa;

    @JoinColumn(name = "FK_USUARIO_CADASTRO", foreignKey = @ForeignKey(name = "FK_PERMISSAO_ESPECIAL_USU_CAD"),
            referencedColumnName = "id", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuarioCadastro;

    @JoinColumn(name = "FK_USUARIO_BAIXA", foreignKey = @ForeignKey(name = "FK_PERMISSAO_ESPECIAL_USU_BAI"),
            referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuarioBaixa;

    public PermissaoEspecial() {
    }

    public PermissaoEspecial(Integer usuarioId, Integer funcionalidadeId) {
        this.usuario = new Usuario(usuarioId);
        this.funcionalidade = new Funcionalidade(funcionalidadeId);
        this.setDataCadastro(LocalDateTime.now());
        this.usuarioCadastro = new Usuario(usuarioId);
    }
}
