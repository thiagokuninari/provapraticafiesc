package br.com.xbrain.autenticacao.modules.horarioacesso.dto;

import br.com.xbrain.autenticacao.modules.horarioacesso.predicate.HorarioAcessoPredicate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioAcessoFiltros {
    private Integer siteId;

    public HorarioAcessoPredicate toPredicate() {
        return new HorarioAcessoPredicate().comSite(siteId);
    }
}
