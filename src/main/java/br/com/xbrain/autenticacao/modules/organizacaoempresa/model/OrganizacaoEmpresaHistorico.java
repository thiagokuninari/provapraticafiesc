package br.com.xbrain.autenticacao.modules.organizacaoempresa.model;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EHistoricoAcao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORGANIZACAO_EMPRESA_HIST")
@EqualsAndHashCode(of = "id")
public class OrganizacaoEmpresaHistorico {

    @Id
    @SequenceGenerator(name = "SEQ_ORGANIZACAO_EMPRESA_HIST", sequenceName = "SEQ_ORGANIZACAO_EMPRESA_HIST",
        allocationSize = 1)
    @GeneratedValue(generator = "SEQ_ORGANIZACAO_EMPRESA_HIST", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "SITUACAO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacaoOrganizacaoEmpresa situacao;

    @Column(name = "OBSERVACAO")
    @Enumerated(EnumType.STRING)
    private EHistoricoAcao observacao;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_ORGANIZACAO_EMPRESA",
        referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_ORG_EMP_HIST"),
        nullable = false)
    private OrganizacaoEmpresa organizacaoEmpresa;

    @Column(name = "DATA_ALTERACAO", nullable = false)
    private LocalDateTime dataAlteracao;

    @Column(name = "USUARIO_ID")
    private Integer usuarioId;

    @Column(name =  "USUARIO_NOME")
    private String usuarioNome;

    public static OrganizacaoEmpresaHistorico of(OrganizacaoEmpresa organizacaoEmpresa, EHistoricoAcao observacao,
                                                 UsuarioAutenticado usuarioAutenticado) {
        var historico = new OrganizacaoEmpresaHistorico();
        historico.setSituacao(organizacaoEmpresa.getSituacao());
        historico.setOrganizacaoEmpresa(organizacaoEmpresa);
        historico.setObservacao(observacao);
        historico.setDataAlteracao(LocalDateTime.now());
        historico.setUsuarioId(usuarioAutenticado.getId());
        historico.setUsuarioNome(usuarioAutenticado.getNome());
        return historico;
    }
}
