package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoConfiguracao;
import br.com.xbrain.autenticacao.modules.usuario.predicate.ConfiguracaoAgendaPredicate;
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
    @NotNull
    private ETipoConfiguracao tipoConfiguracao;
    private Integer qtdHorasAdicionais;
    private String descricao;
    private CodigoNivel nivel;
    private ECanal canal;
    private ETipoCanal subcanal;
    private String estruturaAa;
    private ESituacao situacao;

    public ConfiguracaoAgendaPredicate toPredicate() {
        var predicate = new ConfiguracaoAgendaPredicate()
            .comQtdHorasAdicionais(qtdHorasAdicionais)
            .comTipoConfiguracao(tipoConfiguracao)
            .comDescricao(descricao)
            .comSituacao(situacao);
        aplicarParametrosByTipoConfiguracao(predicate);
        return predicate;
    }

    private void aplicarParametrosByTipoConfiguracao(ConfiguracaoAgendaPredicate predicate) {
        tipoConfiguracao.getPredicateConsumer()
            .accept(predicate, this);
    }
}
