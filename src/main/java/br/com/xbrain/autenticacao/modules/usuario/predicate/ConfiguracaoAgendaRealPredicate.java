package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoConfiguracao;
import org.apache.commons.lang.StringUtils;

import static br.com.xbrain.autenticacao.modules.usuario.model.QConfiguracaoAgendaReal.configuracaoAgendaReal;

public class ConfiguracaoAgendaRealPredicate extends PredicateBase {

    public ConfiguracaoAgendaRealPredicate comNivel(CodigoNivel nivel) {
        if (nivel != null) {
            builder.and(configuracaoAgendaReal.nivel.eq(nivel));
        }
        return this;
    }

    public ConfiguracaoAgendaRealPredicate comCanal(ECanal canal) {
        if (canal != null) {
            builder.and(configuracaoAgendaReal.canal.eq(canal));
        }
        return this;
    }

    public ConfiguracaoAgendaRealPredicate comQtdHorasAdicionais(Integer qtdHorasAdicionais) {
        if (qtdHorasAdicionais != null) {
            builder.and(configuracaoAgendaReal.qtdHorasAdicionais.eq(qtdHorasAdicionais));
        }
        return this;
    }

    public ConfiguracaoAgendaRealPredicate comSubCanal(Integer subcanalId) {
        if (subcanalId != null) {
            builder.and(configuracaoAgendaReal.subcanalId.eq(subcanalId));
        }
        return this;
    }

    public ConfiguracaoAgendaRealPredicate comEstruturaAa(String estruturaAa) {
        if (StringUtils.isNotBlank(estruturaAa)) {
            builder.and(configuracaoAgendaReal.estruturaAa.equalsIgnoreCase(estruturaAa));
        }
        return this;
    }

    public ConfiguracaoAgendaRealPredicate comSituacao(ESituacao situacao) {
        if (situacao != null) {
            builder.and(configuracaoAgendaReal.situacao.eq(situacao));
        }
        return this;
    }

    public ConfiguracaoAgendaRealPredicate comTipoConfiguracao(ETipoConfiguracao tipoConfiguracao) {
        if (tipoConfiguracao != null) {
            builder.and(configuracaoAgendaReal.tipoConfiguracao.eq(tipoConfiguracao));
        }
        return this;
    }

    public ConfiguracaoAgendaRealPredicate comConfiguracaoPadrao(Boolean configuracaoPadrao) {
        if (configuracaoPadrao != null) {
            builder.and(configuracaoAgendaReal.tipoConfiguracao.isNull());
        }
        return this;
    }
}
