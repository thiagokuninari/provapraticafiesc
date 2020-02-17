package br.com.xbrain.autenticacao.modules.usuario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "USUARIO_AFASTAMENTO")
@Entity
public class UsuarioAfastamento {

    @Id
    @SequenceGenerator(name = "SEQ_USUARIO_AFASTAMENTO", sequenceName = "SEQ_USUARIO_AFASTAMENTO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_USUARIO_AFASTAMENTO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_USUARIO_AFASTAMENTO_USUARIO"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @NotNull
    @Column(name = "DATA_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    @NotNull
    @Column(name = "INICIO", nullable = false)
    private LocalDate inicio;

    @Column(name = "FIM", nullable = true)
    private LocalDate fim;

    public static UsuarioAfastamento of(Usuario usuario, LocalDate inicio, LocalDate fim) {
        return UsuarioAfastamento
                .builder()
                .dataCadastro(LocalDateTime.now())
                .usuario(usuario)
                .inicio(inicio)
                .fim(fim)
                .build();
    }
}
