package br.com.xbrain.autenticacao.modules.horarioacesso.model;

import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HORARIO_ACESSO")
public class HorarioAcesso {

    @Id
    @SequenceGenerator(name = "SEQ_HORARIO_ACESSO", sequenceName = "SEQ_HORARIO_ACESSO", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_HORARIO_ACESSO", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @JoinColumn(name = "FK_SITE", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_HORARIO_ACESSO_SITE"))
    @OneToOne(fetch = FetchType.LAZY)
    private Site site;

    @Column(name = "DATA_ULTIMA_ALTERACAO", nullable = false)
    private LocalDateTime dataUltimaAlteracao;

    @Column(name = "USUARIO_ALTERACAO", nullable = false)
    private Usuario usuarioAlteracao;

    @OneToMany(mappedBy = "horario_acesso", fetch = FetchType.LAZY)
    private List<HorarioAcessoDia> dias;

}