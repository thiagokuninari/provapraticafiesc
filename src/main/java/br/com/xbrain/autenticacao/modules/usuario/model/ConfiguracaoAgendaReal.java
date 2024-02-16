package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoConfiguracao;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_CONFIGURACAO")
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

    @Column(name = "USUARIO_CADASTRO_NOME")
    private String usuarioCadastroNome;

    @Column(name = "USUARIO_CADASTRO_ID")
    private Integer usuarioCadastroId;

    @Column(name = "DATA_CADASTRO")
    private LocalDateTime dataCadastro;

    public static ConfiguracaoAgendaReal of(ConfiguracaoAgendaRequest request, UsuarioAutenticado usuario) {
        var configuracao = ConfiguracaoAgendaReal.builder()
            .qtdHorasAdicionais(request.getQtdHorasAdicionais())
            .tipoConfiguracao(request.getTipoConfiguracao())
            .usuarioCadastroNome(usuario.getNome())
            .usuarioCadastroId(usuario.getId())
            .dataCadastro(LocalDateTime.now())
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

    public void validarConfiguracaoPadrao() {
        if (tipoConfiguracao == null) {
            throw new ValidacaoException("Não é possível alterar a situação da configuração padrão.");
        }
    }
}
