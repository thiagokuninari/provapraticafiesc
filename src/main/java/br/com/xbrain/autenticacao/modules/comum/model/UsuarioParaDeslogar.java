package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "USUARIOS_PARA_DESLOGAR")
public class UsuarioParaDeslogar {

    @Id
    @SequenceGenerator(name = "SEQ_USUARIOS_DESLOGAR",
        sequenceName = "SEQ_USUARIOS_DESLOGAR", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_USUARIOS_DESLOGAR", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "USUARIO_ID")
    private Integer usuarioId;

    @Column(name = "DATA_CADASTRO")
    private LocalDateTime dataCadastro;

    @Column(name = "DESLOGADO")
    @Enumerated(EnumType.STRING)
    private Eboolean deslogado;

    @Column(name = "BLOQUEADO")
    @Enumerated(EnumType.STRING)
    private Eboolean bloqueado;

    public UsuarioParaDeslogar atualizarParaDeslogado() {
        deslogado = Eboolean.V;
        return this;
    }

    public UsuarioParaDeslogar atualizarSituacaoBloqueado() {
        bloqueado = Eboolean.F;
        return this;
    }

    public static UsuarioParaDeslogar of(Integer usuarioId, Eboolean bloqueado) {
        return UsuarioParaDeslogar.builder()
            .id(usuarioId)
            .bloqueado(bloqueado)
            .build();
    }
}
