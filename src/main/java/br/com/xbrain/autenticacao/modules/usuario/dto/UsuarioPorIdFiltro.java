package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPorIdFiltro {

    @NotEmpty
    private List<Integer> usuariosIds;

    public UsuarioPredicate toPredicate() {
        return new UsuarioPredicate()
            .comIds(usuariosIds);
    }
}
