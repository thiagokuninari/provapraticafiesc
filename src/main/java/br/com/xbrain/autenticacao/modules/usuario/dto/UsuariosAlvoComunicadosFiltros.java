package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UsuariosAlvoComunicadosFiltros {
    private boolean todoCanalD2d;
    private boolean todoCanalAa;
    private List<Integer> agentesAutorizadosId;
    private List<Integer> usuariosId;
    private List<Integer> cargosId;
    private List<Integer> cidadesId;
    private List<Integer> niveisId;
    private boolean comUsuariosLogadosHoje;
    private Integer clusterId;
    private Integer grupoId;
    private Integer regionalId;
    private Integer subClusterId;

    @JsonIgnore
    public BooleanBuilder toPredicate() {
        return new UsuarioPredicate().comCanalD2d(isTodoCanalD2d())
                .comCanalAa(isTodoCanalAa())
                .comUsuariosId(getUsuariosId())
                .comCargosId(getCargosId())
                .comCidadesId(getCidadesId(), clusterId, grupoId, regionalId, subClusterId)
                .comNiveisId(getNiveisId())
                .comUsuariosLogadosHoje(comUsuariosLogadosHoje)
                .build();
    }
}
