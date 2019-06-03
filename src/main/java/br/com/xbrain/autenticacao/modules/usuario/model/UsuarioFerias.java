package br.com.xbrain.autenticacao.modules.usuario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIO_FERIAS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioFerias {

    @Id
    @SequenceGenerator(name = "SEQ_USUARIO_FERIAS", sequenceName = "SEQ_USUARIO_FERIAS", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_USUARIO_FERIAS", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_USUARIO_FERIAS"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @NotNull
    @Column(name = "DATA_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    @NotNull
    @Column(name = "INICIO", nullable = false)
    private LocalDate inicio;

    @NotNull
    @Column(name = "FIM", nullable = false)
    private LocalDate fim;

    public static UsuarioFerias of(Usuario usuario, LocalDate inicio, LocalDate fim) {
        return UsuarioFerias
                .builder()
                .dataCadastro(LocalDateTime.now())
                .usuario(usuario)
                .inicio(inicio)
                .fim(fim)
                .build();
    }
}
