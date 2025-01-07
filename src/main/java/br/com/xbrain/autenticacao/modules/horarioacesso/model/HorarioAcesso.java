package br.com.xbrain.autenticacao.modules.horarioacesso.model;

import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.*;

import java.time.LocalDateTime;
import javax.persistence.*;

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
    @JoinColumn(name = "FK_SITE", referencedColumnName = "ID", nullable = false, unique = true)
    private Site site;

    @Column(name = "DATA_ALTERACAO")
    private LocalDateTime dataAlteracao;

    @Column(name = "USUARIO_ALTERACAO_ID")
    private Integer usuarioAlteracaoId;

    @Column(name = "USUARIO_ALTERACAO_NOME")
    private String usuarioAlteracaoNome;

    public static HorarioAcesso of(HorarioAcessoRequest request) {
        return HorarioAcesso.builder()
            .id(request.getId())
            .site(new Site(request.getSiteId()))
            .build();
    }

    public static HorarioAcesso of(HorarioAcessoResponse response) {
        return HorarioAcesso.builder()
            .id(response.getHorarioAcessoId())
            .site(new Site(response.getSiteId()))
            .build();
    }

    public void setDadosAlteracao(Usuario usuario) {
        this.dataAlteracao = LocalDateTime.now();
        this.usuarioAlteracaoId = usuario.getId();
        this.usuarioAlteracaoNome = usuario.getNome();
    }
}
