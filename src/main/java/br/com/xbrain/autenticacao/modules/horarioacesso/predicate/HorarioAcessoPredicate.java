package br.com.xbrain.autenticacao.modules.horarioacesso.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import org.springframework.util.StringUtils;

import static br.com.xbrain.autenticacao.modules.horarioacesso.model.QHorarioAcesso.horarioAcesso;

public class HorarioAcessoPredicate extends PredicateBase {
    public HorarioAcessoPredicate comSite(String site) {
        if (!StringUtils.isEmpty(site)) {
            builder.and(horarioAcesso.site.nome.containsIgnoreCase(site));
        }
        return this;
    }
}
