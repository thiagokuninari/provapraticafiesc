package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CONFIGURACAO_AGENDA")
public class ConfiguracaoAgenda {

    @Id
    @SequenceGenerator(name = "SEQ_CONFIGURACAO_AGENDA", sequenceName = "SEQ_CONFIGURACAO_AGENDA", allocationSize = 1)
    @GeneratedValue(generator = "SEQ_CONFIGURACAO_AGENDA", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @NotNull
    @Column(name = "QTD_HORAS", nullable = false)
    private Integer qtdHorasAdicionais;

    @NotBlank
    @Column(name = "DESCRICAO", nullable = false)
    private String descricao;

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

    @Column(name = "SUBCANAL")
    @Enumerated(EnumType.STRING)
    private ETipoCanal subcanal;

    @Column(name = "ESTRUTURA_AA")
    private String estruturaAa;

    public static ConfiguracaoAgenda of(ConfiguracaoAgendaRequest request) {
        var configuracao = new ConfiguracaoAgenda();
        BeanUtils.copyProperties(request, configuracao);
        configuracao.setSituacao(ESituacao.A);
        return configuracao;
    }

    public ConfiguracaoAgenda alterarSituacao(ESituacao novaSituacao) {
        if (this.situacao != novaSituacao) {
            setSituacao(novaSituacao);
            return this;
        }
        throw new ValidacaoException("Configuração já possui a mesma situação.");
    }
}
