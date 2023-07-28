package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioEquipeDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSituacaoResponse;
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
    @Autowired
    private UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<UsuarioNomeResponse> buscarUsuariosSitePorCargo(CodigoCargo cargo) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        if (usuarioAutenticado.isXbrainOuMso()) {
            return repository.buscarUsuariosPorCanalECargo(ECanal.ATIVO_PROPRIO, cargo);
        }
        return repository.findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(usuarioAutenticado.getId(), cargo);
    }

    @Transactional(readOnly = true)
    public List<UsuarioNomeResponse> buscarCoordenadoresDisponiveisEVinculadosAoSite(Integer siteId) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        var sitePredicate = new SitePredicate();
        if (usuarioAutenticado.isXbrainOuMso()) {
            return repository.findCoordenadoresDisponiveisExcetoPorSiteId(sitePredicate.build(), siteId);
        }
        var coordenadoresDisponiveis = repository.findCoordenadoresDisponiveisExcetoPorSiteId(sitePredicate.build(), siteId);
        return filtrarHierarquia(coordenadoresDisponiveis, usuarioAutenticado.getId());
    }

    @Transactional(readOnly = true)
    public List<UsuarioNomeResponse> buscarCoordenadoresDisponiveis() {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        var sitePredicate = new SitePredicate();
        if (usuarioAutenticado.isXbrainOuMso()) {
            return repository.findCoordenadoresDisponiveis(sitePredicate.build());
        }
        return filtrarHierarquia(repository
            .findCoordenadoresDisponiveis(sitePredicate.build()), usuarioAutenticado.getId());
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
        var usuario = getUsuarioById(usuarioId);
        var vendedores = usuario.isXbrainOuMso()
                ? getVendedorPorSiteId(siteId)
                : getVendedoresPorCargoUsuario(usuario, siteId);

        return vendedores
            .stream()
            .map(UsuarioEquipeDto::of)
            .filter(usuarioEquipe -> isAssistenteOperacao(usuario) || validarPermissaoSobreUsuario(usuarioEquipe, usuario))
            .filter(usuarioEquipeDto -> usuarioEquipeDto.isAtivo(buscarInativos))
            .map(this::getEquipeById)
            .collect(Collectors.toList());
    }

    public List<SelectResponse> getVendoresSelectDoSiteIdPorHierarquiaDoUsuarioLogado(Integer siteId,
                                                                                      Boolean buscarInativos) {

        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        return usuarioAutenticado.isOperadorTelevendasAtivoLocal()
            ? List.of(SelectResponse.of(usuarioAutenticado.getId(), usuarioAutenticado.getNome()))
            : getVendoresDoSiteIdPorHierarquiaComEquipe(siteId, usuarioAutenticado.getId(), buscarInativos)
            .stream()
            .map(usuario -> SelectResponse.of(usuario.getUsuarioId(), usuario.getUsuarioNome()))
            .collect(Collectors.toList());
    }

    public Boolean validarPermissaoSobreUsuario(UsuarioEquipeDto usuarioEquipeDto, Usuario usuario) {
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
        return equipeVendasService.getEquipeVendasComSupervisor(usuarioEquipeDto.getUsuarioId())
            .stream()
            .findFirst()
            .map(usuarioEquipeDto::setEquipe)
            .orElse(usuarioEquipeDto);
    }

    private List<UsuarioNomeResponse> filtrarHierarquia(List<UsuarioNomeResponse> usuariosDisponiveis,
                                                        Integer usuarioAutenticadoId) {
        var usuariosDaHierarquia = getSubordinadosPorIdECargo(usuarioAutenticadoId, CodigoCargo.COORDENADOR_OPERACAO);
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

    public List<UsuarioNomeResponse> coordenadoresDoSiteId(Integer siteId) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        var coordenadoresResponse = repository.findCoordenadoresDoSiteId(siteId);

        return usuarioAutenticado.isXbrainOuMso()
            ? coordenadoresResponse
            : filtrarHierarquia(coordenadoresResponse, usuarioAutenticado.getId());
    }

    private List<UsuarioSituacaoResponse> getVendedorPorSiteId(Integer siteId) {
        return repository.findVendedoresPorSiteId(siteId)
            .stream()
            .map(UsuarioSituacaoResponse::of)
            .collect(Collectors.toList());
    }

    public List<UsuarioSituacaoResponse> getVendedoresPorCargoUsuario(Usuario usuario, Integer siteId) {
        return isAssistenteOperacao(usuario)
            ? repository.findVendedoresDoSiteIdPorHierarquiaUsuarioId(getCoordenadoresIdsDoUsuarioId(usuario.getId()), siteId)
            : repository.findVendedoresDoSiteIdPorHierarquiaUsuarioId(List.of(usuario.getId()), siteId);
    }

    private boolean isAssistenteOperacao(Usuario usuario) {
        return usuario.getCargo().getCodigo().equals(CodigoCargo.ASSISTENTE_OPERACAO);
    }

    private List<Integer> getCoordenadoresIdsDoUsuarioId(Integer usuarioId) {
        return repository.getSuperioresDoUsuarioPorCargo(usuarioId, CodigoCargo.COORDENADOR_OPERACAO)
            .stream()
            .map(Usuario::getId)
            .collect(Collectors.toList());
    }

    public List<Integer> getUsuariosDaHierarquiaDoUsuarioLogado() {
        return usuarioService.getUsuariosDaHierarquiaAtivoLocalDoUsuarioLogado()
            .stream()
            .map(Usuario::getId)
            .collect(Collectors.toList());
    }

    public List<UsuarioNomeResponse> getVendedoresDaHierarquiaPorSite(Integer siteId,
                                                                      Boolean buscarInativos) {
        var usuario = autenticacaoService.getUsuarioAutenticado();
        var vendedores = usuario.isXbrainOuMso()
            ? getVendedorPorSiteId(siteId)
            : getVendedoresPorCargoUsuario(getUsuarioById(usuario.getId()), siteId);

        return vendedores
            .stream()
            .map(UsuarioEquipeDto::of)
            .filter(usuarioEquipeDto -> usuarioEquipeDto.isAtivo(buscarInativos))
            .map(UsuarioNomeResponse::of)
            .collect(Collectors.toList());
    }
}
