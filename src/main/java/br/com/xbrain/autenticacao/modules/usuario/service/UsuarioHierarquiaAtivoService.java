package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioEquipeDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHierarquiaFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioHierarquiaAtivoService implements IUsuarioHierarquia {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private UsuarioSiteService usuarioSiteService;

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getUsuariosDaHierarquia(Integer usuarioId, CodigoCargo cargo) {
        return usuarioRepository.findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(usuarioId, cargo)
            .stream()
            .map(UsuarioNomeResponse::getId)
            .collect(Collectors.toList());

    }

    @Override
    public List<UsuarioNomeResponse> coordenadoresSubordinadosHierarquia(UsuarioHierarquiaFiltros usuarioHierarquiaFiltros) {
        return usuarioSiteService.coordenadoresDoSiteId(usuarioHierarquiaFiltros.getSiteId());
    }

    @Override
    public List<UsuarioNomeResponse> supervisoresDaHierarquia(UsuarioHierarquiaFiltros usuarioHierarquiaFiltros) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        var sitePredicate = new SitePredicate()
            .comUsuarioSuperior(usuarioHierarquiaFiltros.getCoordenadorId());
        var supervisores = usuarioRepository
            .findSupervisoresDoSiteIdVinculadoAoCoordenador(usuarioHierarquiaFiltros.getSiteId(),
                sitePredicate.build());

        return usuarioAutenticado.isXbrainOuMso()
            ? supervisores
            : filtrarHierarquia(supervisores, CodigoCargo.SUPERVISOR_OPERACAO);
    }

    @Override
    public List<UsuarioNomeResponse> vendedoresDaHierarquia(UsuarioHierarquiaFiltros usuarioHierarquiaFiltros) {
        if (usuarioHierarquiaFiltros.apenasSiteId()) {
            var vendedores = usuarioSiteService.getVendedoresDaHierarquiaPorSite(usuarioHierarquiaFiltros.getSiteId(),
                usuarioHierarquiaFiltros.getBuscarInativo());
            adicionaInativoNomeDoUsuario(vendedores);
            return vendedores;
        } else {
            var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
            var vendedores = usuarioSiteService.getVendoresDoSiteIdPorHierarquiaComEquipe(usuarioHierarquiaFiltros.getSiteId(),
                usuarioAutenticado.getId(), usuarioHierarquiaFiltros.getBuscarInativo());
            var vendedoresFiltrados = filtrarUsuariosPorEquipes(vendedores, usuarioHierarquiaFiltros.getEquipeVendaId());
            adicionaInativoNomeDoUsuario(vendedoresFiltrados);
            return vendedoresFiltrados;
        }
    }

    private List<UsuarioNomeResponse> filtrarUsuariosPorEquipes(List<UsuarioEquipeDto> usuarioNomeResponses, Integer equipeId) {

        return Optional.ofNullable(equipeId)
            .map(equipe -> usuarioNomeResponses.stream()
                .filter(usuarioEquipe -> validarEquipe(equipe, usuarioEquipe))
                .map(UsuarioNomeResponse::of)
                .collect(Collectors.toList()))
            .orElse(usuarioNomeResponses.stream()
                .map(UsuarioNomeResponse::of)
                .collect(Collectors.toList()));
    }

    private boolean validarEquipe(Integer equipe, UsuarioEquipeDto usuarioEquipe) {
        var usuarioEquipeId = usuarioEquipe.getEquipeVendaId();
        return Objects.nonNull(usuarioEquipeId) && usuarioEquipeId.equals(equipe);
    }

    private void adicionaInativoNomeDoUsuario(List<UsuarioNomeResponse> usuarioNomeResponses) {
        usuarioNomeResponses.forEach(
            usuario -> {
                if (usuario.getSituacao().equals(ESituacao.I)) {
                    usuario.setNome(usuario.getNome().concat(" (INATIVO)"));
                }
            }
        );
    }

    @Override
    public List<UsuarioNomeResponse> filtrarHierarquia(List<UsuarioNomeResponse> usuariosDisponiveis,
                                                        CodigoCargo codigoCargo) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        var usuariosDaHierarquia = getUsuariosDaHierarquia(usuarioAutenticado.getId(),
            codigoCargo);

        return usuariosDisponiveis
            .stream()
            .filter(usuarioNomeResponse -> usuariosDaHierarquia.contains(usuarioNomeResponse.getId()))
            .collect(Collectors.toList());
    }

    @Override
    public void validarCanal(UsuarioHierarquiaFiltros usuarioHierarquiaFiltros) {
        Optional.ofNullable(usuarioHierarquiaFiltros.getSiteId())
            .orElseThrow(() -> new ValidacaoException("Site não encontrado."));
    }

}
