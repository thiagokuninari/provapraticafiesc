package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioEquipeDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioSiteService {

    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private EquipeVendaD2dService equipeVendasService;

    @Transactional(readOnly = true)
    public List<UsuarioNomeResponse> buscarUsuariosSitePorCargo(CodigoCargo cargo) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        if (usuarioAutenticado.isXbrainOuMso()) {
            return repository.buscarUsuariosPorCanalECargo(ECanal.ATIVO_PROPRIO, cargo);
        }
        return repository.findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(usuarioAutenticado.getId(), cargo);
    }

    @Transactional(readOnly = true)
    public List<UsuarioNomeResponse> buscarCoordenadoresDisponiveisEVinculadosAoSite(Integer siteId, List<Integer> cidadesIds) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        var sitePredicate = new SitePredicate().comCoordenadoresComCidade(cidadesIds);
        if (usuarioAutenticado.isXbrainOuMso()) {
            return repository.findCoordenadoresDisponiveisExetoPorSiteId(sitePredicate.build(), siteId);
        }
        var coordenadoresDisponiveis = repository.findCoordenadoresDisponiveisExetoPorSiteId(sitePredicate.build(), siteId);
        return filtrarHierarquia(coordenadoresDisponiveis, usuarioAutenticado);
    }

    @Transactional(readOnly = true)
    public List<UsuarioNomeResponse> getCoordenadoresDisponiveisPorCidade(List<Integer> cidadesIds) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        var sitePredicate = new SitePredicate();
        sitePredicate.comCoordenadoresComCidade(cidadesIds);
        if (usuarioAutenticado.isXbrainOuMso()) {
            return repository.findCoordenadoresDisponiveis(sitePredicate.build());
        }
        return filtrarHierarquia(repository
            .findCoordenadoresDisponiveis(sitePredicate.build()), usuarioAutenticado);
    }

    public List<UsuarioNomeResponse> getVendedoresOperacaoAtivoProprioPorSiteId(Integer siteId) {
        return repository.findVendedoresPorSiteId(siteId);
    }

    @Transactional(readOnly = true)
    public List<Integer> getSubordinadosPorIdECargo(Integer usuarioId, CodigoCargo codigoCargo) {
        return repository.findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(usuarioId, codigoCargo)
            .stream()
            .map(UsuarioNomeResponse::getId)
            .collect(Collectors.toList());
    }

    public List<UsuarioNomeResponse> getSupervisoresSemSitePorCoordenadorsId(List<Integer> coordenadoresIds) {
        var sitePredicate = new SitePredicate()
            .comSupervisoresDisponiveisDosCoordenadores(coordenadoresIds);
        return repository.findSupervisoresSemSitePorCoordenadorId(sitePredicate.build());
    }

    public List<UsuarioNomeResponse> buscarSupervisoresDisponiveisEVinculadosAoSite(List<Integer> coordenadoresIds,
                                                                                    Integer siteId) {
        var sitePredicate = new SitePredicate()
            .comSupervisoresDisponiveisDosCoordenadoresEsite(coordenadoresIds, siteId);
        return repository.findSupervisoresSemSitePorCoordenadorId(sitePredicate.build());
    }

    public List<UsuarioEquipeDto> getVendoresDoSiteIdPorHierarquiaComEquipe(Integer siteId,
                                                                            Integer usuarioId,
                                                                            Boolean buscarInativos) {
        var usuarioNomeResponseList = repository.findVendedoresDoSiteIdPorHierarquiaUsuarioId(usuarioId, siteId);

        return usuarioNomeResponseList
            .stream()
            .map(UsuarioEquipeDto::of)
            .filter(usuarioEquipe -> validarPermissaoSobreUsuario(usuarioEquipe, usuarioId))
            .filter(usuarioEquipeDto -> usuarioEquipeDto.isAtivo(buscarInativos))
            .map(this::getEquipeById)
            .collect(Collectors.toList());
    }

    public Boolean validarPermissaoSobreUsuario(UsuarioEquipeDto usuarioEquipeDto, Integer usuarioId) {
        var usuario = getUsuarioById(usuarioId);
        validarPermissaoUsuarioAdminOuMso(usuario);
        return usuario.isXbrainOuMso()
            || repository.getUsuariosSubordinados(usuario.getId())
            .stream()
            .anyMatch(idsHierarquia -> idsHierarquia.equals(usuarioEquipeDto.getUsuarioId()));
    }

    public Usuario getUsuarioById(Integer usuarioId) {
        return repository.findById(usuarioId)
            .orElseThrow(() -> new ValidacaoException("Usuario não encontrado."));
    }

    public UsuarioEquipeDto getEquipeById(UsuarioEquipeDto usuarioEquipeDto) {
        return equipeVendasService.getEquipeVendas(usuarioEquipeDto.getUsuarioId())
            .stream()
            .findFirst()
            .map(usuarioEquipeDto::setEquipe)
            .orElse(usuarioEquipeDto);
    }

    private List<UsuarioNomeResponse> filtrarHierarquia(List<UsuarioNomeResponse> usuariosDisponiveis,
                                                        UsuarioAutenticado usuarioAutenticado) {
        var usuariosDaHierarquia = getSubordinadosPorIdECargo(usuarioAutenticado.getId(), CodigoCargo.COORDENADOR_OPERACAO);
        return usuariosDisponiveis
            .stream()
            .filter(usuarioNomeResponse -> usuariosDaHierarquia.contains(usuarioNomeResponse.getId()))
            .collect(Collectors.toList());
    }

    public void validarPermissaoUsuarioAdminOuMso(Usuario usuario) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        if (usuario.isXbrainOuMso() && !usuarioAutenticado.isXbrainOuMso()) {
            throw new PermissaoException();
        }
    }
}
