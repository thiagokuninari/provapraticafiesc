package br.com.xbrain.autenticacao.modules.horarioacesso.predicate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HorarioAcessoFiltros {
    private String site;

    public HorarioAcessoPredicate toPredicate() {
        return new HorarioAcessoPredicate().comSite(site);
    }
}
