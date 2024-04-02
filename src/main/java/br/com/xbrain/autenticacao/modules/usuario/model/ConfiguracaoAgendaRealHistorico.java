package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.EAcao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static br.com.xbrain.autenticacao.modules.comum.enums.EAcao.ATUALIZACAO;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CONFIGURACAO_AGENDA_HISTORICO")
public class ConfiguracaoAgendaRealHistorico {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_CONFIGURACAO_AGENDA_HIST")
    @SequenceGenerator(name = "SEQ_CONFIGURACAO_AGENDA_HIST", sequenceName = "SEQ_CONFIGURACAO_AGENDA_HIST", allocationSize = 1)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONFIGURACAO_AGENDA_ID", nullable = false, referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "CONFIGURACAO_AGENDA_ID"))
    private ConfiguracaoAgendaReal configuracao;

    @NotNull
    @Column(name = "ACAO", nullable = false)
    @Enumerated(EnumType.STRING)
    private EAcao acao;

    @NotNull
    @Column(name = "DATA_ACAO", nullable = false)
    private LocalDateTime dataAcao;

    @Column(name = "QTD_HORAS_ATUALIZADA")
    private Integer qtdHorasAtualizada;

    @Column(name = "USUARIO_ACAO_ID")
    private Integer usuarioAcaoId;

    @Column(name = "USUARIO_ACAO_NOME")
    private String usuarioAcaoNome;

    public static ConfiguracaoAgendaRealHistorico of(ConfiguracaoAgendaReal configuracao,
                                                     UsuarioAutenticado usuario, EAcao acao) {
        return ConfiguracaoAgendaRealHistorico.builder()
            .usuarioAcaoNome(usuario.getNome())
            .usuarioAcaoId(usuario.getId())
            .dataAcao(LocalDateTime.now())
            .configuracao(configuracao)
            .acao(acao)
            .qtdHorasAtualizada(acao == ATUALIZACAO ? configuracao.getQtdHorasAdicionais() : null)
            .build();
    }
}
