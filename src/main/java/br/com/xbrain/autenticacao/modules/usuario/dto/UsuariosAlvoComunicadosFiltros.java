package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UsuariosAlvoComunicadosFiltros {
    private boolean todoCanalD2d;
    private boolean todoCanalAa;
    private List<Integer> usuariosId;
    private List<Integer> cargosId;
    private List<Integer> cidadesId;
    private List<Integer> niveisId;

    public BooleanBuilder toPredicate() {
        return new UsuarioPredicate().comCanalD2d(isTodoCanalD2d())
                .comCanalAa(isTodoCanalAa())
                .comUsuariosId(getUsuariosId())
                .comCargosId(getCargosId())
                .comCidadesId(getCidadesId())
                .comNiveisId(getNiveisId())
                .comUltimaDataDeAcesso(LocalDate.now())
                .build();
    }
}
