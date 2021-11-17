package br.com.xbrain.autenticacao.modules.horarioacesso.predicate;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioAcessoFiltros {
    private String site;

    public HorarioAcessoPredicate toPredicate() {
        return new HorarioAcessoPredicate().comSite(site);
    }
}
