package br.com.xbrain.autenticacao.modules.comum.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.model.QUnidadeNegocio;

public class UnidadeNegocioPredicate extends PredicateBase {

    public UnidadeNegocioPredicate exibeXbrainSomenteParaXbrain(boolean isXbrain) {
        if (!isXbrain) {
            builder.and(QUnidadeNegocio.unidadeNegocio.codigo.ne(CodigoUnidadeNegocio.XBRAIN));
        }
        return this;
    }
}
