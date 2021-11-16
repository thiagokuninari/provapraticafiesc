package br.com.xbrain.autenticacao.modules.horarioacesso.model;

import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_SITE", referencedColumnName = "ID", nullable = false)
    private Site site;

    @Column(name = "DATA_ALTERACAO", nullable = false)
    private LocalDateTime dataAlteracao;

    @Column(name = "USUARIO_ALTERACAO", nullable = false)
    private Usuario usuarioAlteracao;

    public HorarioAcesso(Integer id) {
        this.id = id;
    }

    public static HorarioAcesso converFrom(HorarioAcessoRequest request) {
        return HorarioAcesso.builder()
            .id(request.getId())
            .site(new Site(request.getSiteId()))
            .build();
    }

    public void setDadosAlteracao(Usuario usuarioAlteracao) {
        this.dataAlteracao = LocalDateTime.now();
        this.usuarioAlteracao = usuarioAlteracao;
    }
}