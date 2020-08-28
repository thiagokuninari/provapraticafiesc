package br.com.xbrain.autenticacao.modules.comum.filtros;

import br.com.xbrain.autenticacao.modules.comum.predicate.OrganizacaoPredicate;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizacaoFiltros {

    private Integer nivelId;
    private Integer organizacaoId;
    private CodigoNivel codigoNivel;

    public BooleanBuilder toPredicate() {
        return new OrganizacaoPredicate()
            .comNivel(nivelId)
            .comId(organizacaoId)
            .comCodigoNivel(codigoNivel)
            .build();
    }
}
