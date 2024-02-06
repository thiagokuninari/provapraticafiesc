package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.model.QConfiguracaoAgenda.configuracaoAgenda;

public class ConfiguracaoAgendaPredicate extends PredicateBase {

    public ConfiguracaoAgendaPredicate comNivel(UsuarioAutenticado usuario) {
        if (!usuario.isOperacao()) {
            builder.and(configuracaoAgenda.nivel.eq(usuario.getNivelCodigoEnum()));
        }
        return this;
    }

    public ConfiguracaoAgendaPredicate comNivel(CodigoNivel nivel) {
        if (nivel != null) {
            builder.and(configuracaoAgenda.nivel.eq(nivel));
        }
        return this;
    }

    public ConfiguracaoAgendaPredicate comCanal(ECanal canal) {
        if (canal != null) {
            builder.and(configuracaoAgenda.canal.eq(canal));
        }
        return this;
    }

    public ConfiguracaoAgendaPredicate comDescricao(String descricao) {
        if (StringUtils.isNotBlank(descricao)) {
            builder.and(configuracaoAgenda.descricao.containsIgnoreCase(descricao));
        }
        return this;
    }

    public ConfiguracaoAgendaPredicate comQtdHorasAdicionais(Integer qtdHorasAdicionais) {
        if (qtdHorasAdicionais != null) {
            builder.and(configuracaoAgenda.qtdHorasAdicionais.eq(qtdHorasAdicionais));
        }
        return this;
    }

    public ConfiguracaoAgendaPredicate comSubCanal(ETipoCanal subcanal) {
        if (subcanal != null) {
            builder.and(configuracaoAgenda.subcanal.eq(subcanal));
        }
        return this;
    }

    public ConfiguracaoAgendaPredicate comEstruturaAa(String estruturaAa) {
        if (StringUtils.isNotBlank(estruturaAa)) {
            builder.and(configuracaoAgenda.estruturaAa.equalsIgnoreCase(estruturaAa));
        }
        return this;
    }

    public ConfiguracaoAgendaPredicate comSituacao(ESituacao situacao) {
        if (situacao != null) {
            builder.and(configuracaoAgenda.situacao.eq(situacao));
        }
        return this;
    }
}
