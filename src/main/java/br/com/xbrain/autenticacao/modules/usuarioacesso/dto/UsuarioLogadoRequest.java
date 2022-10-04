package br.com.xbrain.autenticacao.modules.usuarioacesso.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioLogadoRequest {

    private List<PaLogadoDto> periodos;
    private List<CodigoCargo> cargos;
    private Integer organizacaoId;
    private List<Integer> usuariosIds;

    @JsonIgnore
    public BooleanBuilder toUsuarioPredicate() {
        return new UsuarioPredicate()
            .comOrganizacaoEmpresaId(organizacaoId)
            .comCodigosCargos(cargos)
            .isAtivo(Eboolean.V)
            .build();
    }
}
