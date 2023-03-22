package br.com.xbrain.autenticacao.modules.usuario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Table(name = "USUARIO_HIST_LOGIN_SENHA")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSenhaIncorretaHistorico {

    @Id
    @SequenceGenerator(name = "SEQ_USUARIO_SENHA_INC", sequenceName = "SEQ_USUARIO_SENHA_INC", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_USUARIO_SENHA_INC", strategy = GenerationType.SEQUENCE)
    private Integer id;

    private LocalDate dataTentativa;

    @NotNull
    @JsonIgnore
    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_USUARIO_SENHA_INCORRETA"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

}
