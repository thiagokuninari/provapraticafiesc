package br.com.xbrain.autenticacao.modules.usuarioacesso.model;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIO_ACESSO")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsuarioAcesso {

    @Id
    @SequenceGenerator(name = "SEQ_USUARIO_ACESSO", sequenceName = "SEQ_USUARIO_ACESSO")
    @GeneratedValue(generator = "SEQ_USUARIO_ACESSO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Column(name = "DATA_CADASTRO", updatable = false)
    private LocalDateTime dataCadastro;

    @NotNull
    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID", foreignKey = @ForeignKey(name = "FK_USUARIO"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    public UsuarioAcesso(LocalDateTime dataCadastro, Integer usuarioId, String usuarioEmail) {
        this.dataCadastro = dataCadastro;
        this.usuario = Usuario.builder()
                .id(usuarioId)
                .email(usuarioEmail)
                .build();
    }

    public UsuarioAcesso criaRegistroAcesso(Integer usuarioId) {
        return UsuarioAcesso.builder()
                .dataCadastro(LocalDateTime.now())
                .usuario(new Usuario(usuarioId))
                .build();
    }

    public static UsuarioAcesso of(Usuario usuario) {
        return UsuarioAcesso.builder()
                .usuario(usuario)
                .build();
    }
}
