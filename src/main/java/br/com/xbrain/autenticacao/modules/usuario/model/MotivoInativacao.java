package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "MOTIVO_INATIVACAO")
@Data
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MotivoInativacao {

    @Id
    private Integer id;

    @NotNull
    @Size(max = 250)
    @Column(name = "DESCRICAO", nullable = false, length = 250)
    private String descricao;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CodigoMotivoInativacao codigo;

    @NotNull
    @Column(name = "SITUACAO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

}
