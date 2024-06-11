package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.EAcao;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalCompletDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "SUB_CANAL_HISTORICO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubCanalHistorico {

    @Id
    @SequenceGenerator(name = "SEQ_SUB_CANAL_HIST", sequenceName = "SEQ_SUB_CANAL_HIST", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_SUB_CANAL_HIST", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_SUB_CANAL", referencedColumnName = "ID", unique = true)
    private SubCanal subCanal;

    @Column(name = "CODIGO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ETipoCanal codigo;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "SITUACAO", nullable = false)
    @Enumerated(EnumType.STRING)
    private ESituacao situacao;

    @Column(name = "NOVA_CHECAGEM_CRED_ANTIGA", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private Eboolean novaChecagemCreditoAntiga;

    @Column(name = "NOVA_CHECAGEM_VIAB_ANTIGA", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private Eboolean novaChecagemViabilidadeAntiga;

    @Column(name = "NOVA_CHECAGEM_CRED_NOVA", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private Eboolean novaChecagemCreditoNova;

    @Column(name = "NOVA_CHECAGEM_VIAB_NOVA", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private Eboolean novaChecagemViabilidadeNova;

    @NotNull
    @Column(name = "ACAO", nullable = false)
    @Enumerated(EnumType.STRING)
    private EAcao acao;

    @NotNull
    @Column(name = "DATA_ACAO", nullable = false)
    private LocalDateTime dataAcao;

    @Column(name = "USUARIO_ACAO_ID")
    private Integer usuarioAcaoId;

    @Column(name = "USUARIO_ACAO_NOME")
    private String usuarioAcaoNome;

    public static SubCanalHistorico of(SubCanal subCanalAntigo,
                          SubCanalCompletDto subCanalNovo,
                          UsuarioAutenticado usuarioAutenticado) {
        var subCanalHistorico = new SubCanalHistorico();
        subCanalHistorico.setCodigo(subCanalNovo.getCodigo());
        subCanalHistorico.setNome(subCanalNovo.getNome());
        subCanalHistorico.setSituacao(subCanalNovo.getSituacao());
        subCanalHistorico.setNovaChecagemCreditoAntiga(subCanalAntigo.getNovaChecagemCredito());
        subCanalHistorico.setNovaChecagemViabilidadeAntiga(subCanalAntigo.getNovaChecagemViabilidade());
        subCanalHistorico.setNovaChecagemCreditoNova(subCanalNovo.getNovaChecagemCredito());
        subCanalHistorico.setNovaChecagemViabilidadeNova(subCanalNovo.getNovaChecagemViabilidade());
        subCanalHistorico.setAcao(EAcao.ATUALIZACAO);
        subCanalHistorico.setDataAcao(LocalDateTime.now());
        subCanalHistorico.setUsuarioAcaoId(usuarioAutenticado.getId());
        subCanalHistorico.setUsuarioAcaoNome(usuarioAutenticado.getNome());
        return subCanalHistorico;
    }
}
