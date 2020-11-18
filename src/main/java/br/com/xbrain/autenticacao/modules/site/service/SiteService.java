package br.com.xbrain.autenticacao.modules.site.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.site.dto.SiteFiltros;
import br.com.xbrain.autenticacao.modules.site.dto.SiteRequest;
import br.com.xbrain.autenticacao.modules.site.dto.SiteResponse;
import br.com.xbrain.autenticacao.modules.site.dto.SiteSupervisorResponse;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
import br.com.xbrain.autenticacao.modules.site.repository.SiteRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHierarquiaResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSubordinadoDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.site.enums.EHierarquiaSite.getHierarquia;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional
public class SiteService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Site não encontrado.");
    private static final ValidacaoException EX_SITE_EXISTENTE = new ValidacaoException("Site já cadastrado no sistema.");
    private static final ValidacaoException EX_CIDADE_VINCULADA_A_OUTRO_SITE =
        new ValidacaoException("Existem cidades vinculadas à outro site.");
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private UfRepository ufRepository;
    @Autowired
    private CidadeRepository cidadeRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private CallService callService;
    @Autowired
    private UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public Site findById(Integer id) {
        return siteRepository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    @Transactional(readOnly = true)
    public List<SelectResponse> getAllByUsuarioLogado() {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        return siteRepository.findAll(filtrarPorUsuarioXbrainOuMso(usuarioAutenticado))
            .stream()
            .map(site -> SelectResponse.of(site.getId(), site.getNome()))
            .collect(toList());
    }

    @Transactional(readOnly = true)
    public Page<Site> getAll(SiteFiltros filtros, PageRequest pageRequest) {
        return siteRepository.findAll(filtrarPorUsuario(filtros.toPredicate()), pageRequest);
    }

    public Collection<Integer> getUsuariosIdsBySiteId(Integer siteId) {
        return usuarioRepository.findUsuariosIdsPorSiteId(siteId);
    }

    public Predicate filtrarPorUsuario(SitePredicate filtros) {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        if (!usuarioAutenticado.hasCanal(ECanal.ATIVO_PROPRIO) && !usuarioAutenticado.isXbrainOuMso()) {
            return filtros.ignorarTodos().build();
        }
        setFiltrosHierarquia(usuarioAutenticado.getId(), usuarioAutenticado.getCargoCodigo(),
            usuarioAutenticado.getDepartamentoCodigo(), filtros);
        return filtros.build();
    }

    public void setFiltrosHierarquia(Integer usuarioId, CodigoCargo codigoCargo, CodigoDepartamento codigoDepartamento,
                                     SitePredicate filtros) {

        var hierarquia = getHierarquia(codigoCargo, codigoDepartamento);

        switch (hierarquia) {
            case TODOS_VISUALIZAR_EDITAR:
                break;
            case VISUALIZAR_EDITAR_SUBORDINADOS:
                filtros.comCoordenadoresOuSupervisores(getSubordinadosAbaixoDiretor(usuarioId));
                break;
            case VISUALIZAR_DE_SUPERIORES:
                filtros.comCoordenadoresOuSupervisores(getSuperioresDoUsuario(usuarioId));
                break;
            case VISUALIZAR_PROPRIO:
                filtros.comCoordenadoresOuSupervisor(usuarioId);
                break;
            default:
                filtros.ignorarTodos();
        }
    }

    public List<Integer> getSuperioresDoUsuario(Integer usuarioId) {
        return usuarioService.getSuperioresDoUsuario(usuarioId)
                .stream()
                .map(UsuarioHierarquiaResponse::getId)
                .collect(toList());
    }

    public List<Integer> getSubordinadosAbaixoDiretor(Integer usuarioId) {
        return usuarioService.getSubordinadosDoUsuario(usuarioId)
                .stream()
                .map(UsuarioSubordinadoDto::getId)
                .collect(toList());
    }

    private Predicate filtrarPorUsuarioXbrainOuMso(UsuarioAutenticado usuarioAutenticado) {
        var predicate = new SitePredicate();
        if (usuarioAutenticado.isXbrainOuMso()) {
            return predicate.todosSitesAtivos().build();
        }
        return predicate.comCoordenadoresOuSupervisor(usuarioAutenticado.getUsuario().getId()).build();
    }

    @Transactional(readOnly = true)
    public List<SelectResponse> getAllAtivos(SiteFiltros filtros) {
        return siteRepository.findBySituacaoAtiva(filtros.toPredicate().build())
            .stream()
            .map(site -> SelectResponse.of(site.getId(), site.getNome()))
            .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<SiteSupervisorResponse> getAllSupervisoresBySiteId(Integer id) {
        return findById(id)
            .getSupervisores()
            .stream()
            .map(SiteSupervisorResponse::of)
            .collect(toList());
    }

    public List<SiteSupervisorResponse> getAllSupervisoresByHierarquia(Integer siteId, Integer usuarioSuperiorId) {
        var supervisoresSubordinadosIds =
            usuarioService.getIdsSubordinadosDaHierarquia(usuarioSuperiorId, CodigoCargo.SUPERVISOR_OPERACAO.name());

        return findById(siteId)
            .getSupervisores()
            .stream()
            .filter(u -> supervisoresSubordinadosIds.contains(u.getId()))
            .map(SiteSupervisorResponse::of)
            .collect(toList());
    }

    public Site save(SiteRequest request) {
        validarDadosCadastro(request);
        return siteRepository.save(Site.of(request));
    }

    private void validarDadosCadastro(SiteRequest request) {
        verificarExistencia(request);
        verificarInclusaoCidades(request);
    }

    private void verificarExistencia(SiteRequest request) {
        if (siteRepository.findAll()
            .stream()
            .anyMatch(site -> StringUtil.existeSemelhancaEntreNomes(site.getNome(), request.getNome())
                && !site.getId().equals(request.getId()))) {
            throw EX_SITE_EXISTENTE;
        }
    }

    public Site update(SiteRequest request) {
        validarDadosCadastro(request);
        var site = findById(request.getId());
        site.update(request);
        return site;
    }

    public void inativar(Integer id) {
        var site = findById(id);
        if (Objects.equals(site.getSituacao(), A)) {
            site.inativar();
        }
    }

    public List<SelectResponse> buscarEstadosNaoAtribuidosEmSites(Integer siteExclusoId) {
        return Optional.ofNullable(siteExclusoId)
            .map(ufRepository::buscarEstadosNaoAtribuidosEmSitesExcetoPor)
            .orElseGet(ufRepository::buscarEstadosNaoAtribuidosEmSites)
            .stream()
            .map(estado -> SelectResponse.of(estado.getId(), estado.getNome()))
            .collect(toList());
    }

    public List<SelectResponse> buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(List<Integer> estadosIds, Integer siteIgnoradoId) {
        return buscarCidadesDisponiveis(estadosIds, siteIgnoradoId)
            .stream()
            .map(cidade -> SelectResponse.of(cidade.getId(), cidade.getNomeComUf()))
            .collect(toList());
    }

    private void verificarInclusaoCidades(SiteRequest siteRequest) {
        if (siteRequest.isIncluirCidadesDisponiveis()) {
            incluirCidadesDisponiveis(siteRequest);
        } else {
            validarCidadesDisponiveis(siteRequest);
        }
    }

    private void incluirCidadesDisponiveis(SiteRequest siteRequest) {
        siteRequest.setCidadesIds(
            buscarCidadesDisponiveis(siteRequest.getEstadosIds(), siteRequest.getId())
                .stream()
                .map(Cidade::getId)
                .collect(toList())
        );
    }

    private void validarCidadesDisponiveis(SiteRequest siteRequest) {
        siteRepository.findFirstByCidadesIdInAndIdNot(siteRequest.getCidadesIds(),
            Optional.ofNullable(siteRequest.getId()).orElse(BigInteger.ZERO.intValue()))
            .ifPresent(site -> {
                throw EX_CIDADE_VINCULADA_A_OUTRO_SITE;
            });
    }

    private List<Cidade> buscarCidadesDisponiveis(List<Integer> estadosIds, Integer siteIgnoradoId) {
        return Optional.ofNullable(siteIgnoradoId)
            .map(id -> cidadeRepository
                .buscarCidadesNaoAtribuidasEmSitesPorEstadosIdsExcetoPor(estadosIds, id))
            .orElseGet(() -> cidadeRepository
                .buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(estadosIds));
    }

    public List<SelectResponse> getSitesByEstadoId(Integer estadoId) {
        return siteRepository.findByEstadoId(estadoId)
            .stream()
            .map(site -> SelectResponse.of(site.getId(), site.getNome()))
            .collect(toList());
    }

    public List<SelectResponse> getSitesPorPermissao(Usuario usuario) {

        var sitePredicate = new SitePredicate();
        setFiltrosHierarquia(usuario.getId(), usuario.getCargoCodigo(), usuario.getDepartamentoCodigo(), sitePredicate);
        return siteRepository.findBySituacaoAtiva(sitePredicate.build())
            .stream()
            .map(site -> SelectResponse.of(site.getId(), site.getNome()))
            .collect(toList());
    }

    public void adicionarDiscadora(Integer discadoraId, List<Integer> sites) {
        siteRepository.updateDiscadoraBySites(discadoraId, sites);
        //todo migrar usuarios para nova discadora
        callService.cleanCacheableSiteAtivoProprio();
    }

    public void removerDiscadora(Integer siteId) {
        var site = findById(siteId);
        callService.desvincularRamaisDaDiscadoraAtivoProprio(site.getId(), site.getDiscadoraId());
        siteRepository.removeDiscadoraBySite(siteId);
        callService.cleanCacheableSiteAtivoProprio();
    }

    public SiteResponse getSiteBySupervisorId(Integer supervisorId) {
        return SiteResponse.of(siteRepository.findBySupervisorId(supervisorId));
    }
}
