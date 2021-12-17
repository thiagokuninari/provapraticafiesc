package br.com.xbrain.autenticacao.modules.horarioacesso.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;

import static br.com.xbrain.autenticacao.modules.horarioacesso.model.QHorarioAcesso.horarioAcesso;
import static java.util.Objects.nonNull;

public class HorarioAcessoPredicate extends PredicateBase {

    public HorarioAcessoPredicate comSite(Integer siteId) {
        if (nonNull(siteId)) {
            builder.and(horarioAcesso.site.id.eq(siteId));
        }
        return this;
    }
}
