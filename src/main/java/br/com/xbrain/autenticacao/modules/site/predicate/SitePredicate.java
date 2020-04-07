package br.com.xbrain.autenticacao.modules.site.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;

import static br.com.xbrain.autenticacao.modules.site.model.QSite.site;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class SitePredicate extends PredicateBase {

    public SitePredicate comNome(String nome) {
        if (isNotBlank(nome)) {
            builder.and(site.nome.likeIgnoreCase("%" + nome + "%"));
        }
        return this;
    }

    public SitePredicate comCidade(String cidade) {
        if (isNotBlank(cidade)) {
            builder.and(site.cidades.any().nome.likeIgnoreCase("%" + cidade + "%"));
        }
        return this;
    }

    public SitePredicate comSituacao(ESituacao situacao) {
        if (nonNull(situacao)) {
            builder.and(site.situacao.eq(situacao));
        }
        return this;
    }
}
