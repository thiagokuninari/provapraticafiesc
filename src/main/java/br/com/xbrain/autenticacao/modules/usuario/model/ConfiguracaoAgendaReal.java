package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoConfiguracao;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CONFIGURACAO_AGENDA_REAL")
public class ConfiguracaoAgendaReal {

    @Id
    @SequenceGenerator(
        name = "SEQ_CONFIGURACAO_AGENDA_REAL",
        sequenceName = "SEQ_CONFIGURACAO_AGENDA_REAL",
        allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CONFIGURACAO_AGENDA_REAL", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Column(name = "QTD_HORAS", nullable = false)
    private Integer qtdHorasAdicionais;

    @NotBlank
    @Column(name = "DESCRICAO", nullable = false)
    private String descricao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_CONFIGURACAO", nullable = false)
    private ETipoConfiguracao tipoConfiguracao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", nullable = false)
    private ESituacao situacao;

    @Column(name = "NIVEL")
    @Enumerated(EnumType.STRING)
    private CodigoNivel nivel;

    @Column(name = "CANAL")
    @Enumerated(EnumType.STRING)
    private ECanal canal;

    @Column(name = "SUBCANAL_ID")
    private Integer subcanalId;

    @Column(name = "ESTRUTURA_AA")
    private String estruturaAa;

    public static ConfiguracaoAgendaReal of(ConfiguracaoAgendaRequest request) {
        var configuracao = ConfiguracaoAgendaReal.builder()
            .qtdHorasAdicionais(request.getQtdHorasAdicionais())
            .tipoConfiguracao(request.getTipoConfiguracao())
            .descricao(request.getDescricao())
            .situacao(ESituacao.A)
            .build();
        configuracao.aplicarParametrosByTipoConfiguracao(request);
        return configuracao;
    }

    private void aplicarParametrosByTipoConfiguracao(ConfiguracaoAgendaRequest request) {
        tipoConfiguracao.getModelConsumer()
            .accept(this, request);
    }

    public void alterarSituacao(ESituacao novaSituacao) {
        if (this.situacao == novaSituacao) {
            throw new ValidacaoException("Configuração já possui a mesma situação.");
        }
        setSituacao(novaSituacao);
    }
}
