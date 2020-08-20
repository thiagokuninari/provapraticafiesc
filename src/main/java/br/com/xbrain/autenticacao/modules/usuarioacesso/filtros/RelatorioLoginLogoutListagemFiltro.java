package br.com.xbrain.autenticacao.modules.usuarioacesso.filtros;

import br.com.xbrain.autenticacao.modules.usuarioacesso.predicate.UsuarioAcessoPredicate;
import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioLoginLogoutListagemFiltro {

    @NotEmpty
    private Set<Integer> colaboradoresIds;

    public BooleanBuilder toPredicate() {
        return new UsuarioAcessoPredicate()
            .porUsuarioIds(colaboradoresIds)
            .build();
    }
}
