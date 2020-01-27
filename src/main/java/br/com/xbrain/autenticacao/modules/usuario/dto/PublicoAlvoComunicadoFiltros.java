package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PublicoAlvoComunicadoFiltros {
    private boolean todoCanalD2d;
    private boolean todoCanalAa;
    private List<Integer> agentesAutorizadosId;
    private List<Integer> usuariosId;
    private List<Integer> cargosId;
    private List<Integer> cidadesId;
    private List<Integer> niveisId;
    private Integer clusterId;
    private Integer grupoId;
    private Integer regionalId;
    private Integer subClusterId;

    private UsuarioAutenticado usuarioAutenticado;
    private UsuarioService usuarioService;
    private boolean comUsuariosLogadosHoje;

    @JsonIgnore
    public Predicate toPredicate() {
        return new UsuarioPredicate()
            .comCanalD2d(isTodoCanalD2d())
            .comCanalAa(isTodoCanalAa())
            .comUsuariosId(getUsuariosId())
            .comCargosId(getCargosId())
            .comCidadesId(getCidadesId(), clusterId, grupoId, regionalId, subClusterId)
            .comNiveisId(getNiveisId())
            .comUsuariosLogadosHoje(comUsuariosLogadosHoje)
            .comCluster(clusterId)
            .comSituacao(null, false)
            .comGrupo(grupoId)
            .comRegional(regionalId)
            .comSubCluster(subClusterId)
            .filtraPermitidos(usuarioAutenticado, usuarioService)
            .build();
    }
}
