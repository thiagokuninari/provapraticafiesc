package br.com.xbrain.autenticacao.modules.comum.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;

import java.util.Objects;

import static br.com.xbrain.autenticacao.modules.comum.model.QOrganizacao.organizacao;

public class OrganizacaoPredicate extends PredicateBase {

    public OrganizacaoPredicate comNivel(Integer nivel) {
        if (Objects.nonNull(nivel)) {
            builder.and(organizacao.niveis.any().id.eq(nivel));
        }
        return this;
    }

    public OrganizacaoPredicate comId(Integer id) {
        if (Objects.nonNull(id)) {
            builder.and(organizacao.id.eq(id));
        }
        return this;
    }

    public OrganizacaoPredicate comCodigoNivel(CodigoNivel codigoNivel) {
        if (Objects.nonNull(codigoNivel)) {
            builder.and(organizacao.niveis.any().codigo.eq(codigoNivel));
        }
        return this;
    }
}
