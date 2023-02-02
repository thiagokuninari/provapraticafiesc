package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.isNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PublicoAlvoComunicadoFiltros {
    private boolean todoCanalD2d;
    private boolean todoCanalAa;
    private List<Integer> agentesAutorizadosIds;
    private List<Integer> equipesVendasIds;
    private List<Integer> usuariosIds;
    private List<Integer> cargosIds;
    private List<Integer> cidadesIds;
    private List<Integer> niveisIds;
    private Integer regionalId;
    private Integer ufId;
    private List<Integer> usuariosFiltradosPorCidadePol;

    private UsuarioAutenticado usuarioAutenticado;
    private UsuarioService usuarioService;
    private boolean comUsuariosLogadosHoje;

    @JsonIgnore
    public Predicate toPredicate() {
        return new UsuarioPredicate()
            .comCanalD2d(isTodoCanalD2d())
            .comCanalAa(isTodoCanalAa())
            .comUsuariosIds(getUsuariosIds())
            .comCargosIds(getCargosIds())
            .comNiveisIds(getNiveisIds())
            .comUsuariosLogadosHoje(comUsuariosLogadosHoje)
            .comSituacoes(List.of(ESituacao.A, ESituacao.I))
            .comFiltroCidadeParceiros(usuarioAutenticado, usuarioService, this)
            .semUsuarioId(usuarioAutenticado.getId())
            .filtraPermitidosComParceiros(usuarioAutenticado, usuarioService, this)
            .build();
    }

    public void adicionarUsuariosId(List<Integer> usuariosIdNovos) {
        if (isNull(usuariosIds)) {
            usuariosIds = newArrayList();
        }
        if (!ObjectUtils.isEmpty(usuariosIdNovos)) {
            usuariosIds.addAll(usuariosIdNovos);
        }
    }

    public void tratarFiltrosLocalizacaoParaMelhorDesempenho() {
        if (!ObjectUtils.isEmpty(cidadesIds)) {
            regionalId = null;
            ufId = null;
        } else if (!ObjectUtils.isEmpty(ufId)) {
            regionalId = null;
        }
    }

    public boolean haveFiltrosDeLocalizacao() {
        return !ObjectUtils.isEmpty(cidadesIds)
            || !ObjectUtils.isEmpty(ufId)
            || !ObjectUtils.isEmpty(regionalId);
    }
}
