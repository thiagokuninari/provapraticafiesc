package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoConfiguracao;
import br.com.xbrain.autenticacao.modules.usuario.predicate.ConfiguracaoAgendaRealPredicate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoAgendaFiltros {
    private ETipoConfiguracao tipoConfiguracao;
    private Integer qtdHorasAdicionais;
    private CodigoNivel nivel;
    private ECanal canal;
    private Integer subcanalId;
    private String estruturaAa;
    private ESituacao situacao;

    public ConfiguracaoAgendaRealPredicate toPredicate() {
        var predicate = new ConfiguracaoAgendaRealPredicate()
            .comQtdHorasAdicionais(qtdHorasAdicionais)
            .comTipoConfiguracao(tipoConfiguracao)
            .comSituacao(situacao);
        aplicarParametrosByTipoConfiguracao(predicate);
        return predicate;
    }

    private void aplicarParametrosByTipoConfiguracao(ConfiguracaoAgendaRealPredicate predicate) {
        if (tipoConfiguracao != null && tipoConfiguracao != ETipoConfiguracao.PADRAO) {
            tipoConfiguracao.getPredicateConsumer().accept(predicate, this);
        }
    }
}
