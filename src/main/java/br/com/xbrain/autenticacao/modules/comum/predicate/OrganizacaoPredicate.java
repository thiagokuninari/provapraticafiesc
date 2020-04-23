package br.com.xbrain.autenticacao.modules.comum.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;

import static br.com.xbrain.autenticacao.modules.comum.model.QOrganizacao.organizacao;

public class OrganizacaoPredicate extends PredicateBase {

    public OrganizacaoPredicate comNivel(Integer nivel) {
        builder.and(organizacao.niveis.any().id.eq(nivel));
        return this;
    }

    public OrganizacaoPredicate comId(Integer id) {
        builder.and(organizacao.id.eq(id));
        return this;
    }
}
