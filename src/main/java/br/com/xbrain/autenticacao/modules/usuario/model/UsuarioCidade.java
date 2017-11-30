package br.com.xbrain.autenticacao.modules.usuario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIO_CIDADE")
@Data
public class UsuarioCidade {

    @Id
    @Column(name = "ID")
    @GenericGenerator(
            name = "SEQ_USUARIO_CIDADE",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_USUARIO_CIDADE")})
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_USUARIO_CIDADE")
    private Integer id;

    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO", foreignKey = @ForeignKey(name = "FK_USUARIO_CIDADE_USUARIO"),
            referencedColumnName = "ID", nullable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @JoinColumn(name = "FK_CIDADE", foreignKey = @ForeignKey(name = "FK_USUARIO_CIDADE"),
            referencedColumnName = "ID", nullable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Cidade cidade;

    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO_CADASTRO", foreignKey = @ForeignKey(name = "FK_USUARIO_CIDADE_USUARIO_CAD"),
            referencedColumnName = "ID", nullable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuarioCadastro;

    @NotNull
    @Column(name = "DATA_CADASTRO", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "DATA_BAIXA")
    private LocalDateTime dataBaixa;

    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO_BAIXA", foreignKey = @ForeignKey(name = "FK_USU_CIDADE_USU_BAIXA"),
            referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuarioBaixa;

    @Transient
    private boolean baixar;

    @JsonIgnore
    public boolean isNova() {
        return id == null;
    }

    public UsuarioCidade() {
    }

    public UsuarioCidade(Cidade cidade) {
        this.cidade = cidade;
    }


}