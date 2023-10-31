package br.com.xbrain.autenticacao.modules.site.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.site.dto.*;
import br.com.xbrain.autenticacao.modules.site.enums.EHierarquiaSite;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
import br.com.xbrain.autenticacao.modules.site.repository.SiteRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHierarquiaResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSubordinadoDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CidadeDbmPredicate;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CidadePredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import br.com.xbrain.xbrainutils.CsvUtils;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.service.CidadeService.getListaCidadeResponseOrdenadaPorNome;
import static br.com.xbrain.autenticacao.modules.usuario.service.CidadeService.hasFkCidadeSemNomeCidadePai;

@Slf4j
@Service
@Transactional
public class SiteService {

    private static final NotFoundException EX_NAO_ENCONTRADO =
        new NotFoundException("Site não encontrado.");
    private static final ValidacaoException EX_SITE_EXISTENTE =
        new ValidacaoException("Site já cadastrado anteriormente com esse nome.");
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
    private CidadeService cidadeService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private EquipeVendaD2dService equipeVendaD2dService;

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
            .collect(Collectors.toList());
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
        if (usuarioAutenticado.isXbrainOuMso()) {
            return filtros.build();
        }
        setFiltrosHierarquia(usuarioAutenticado.getId(), usuarioAutenticado.getCargoCodigo(),
            usuarioAutenticado.getDepartamentoCodigo(), filtros);
        return filtros.build();
    }

    public void setFiltrosHierarquia(Integer usuarioId, CodigoCargo codigoCargo, CodigoDepartamento codigoDepartamento,
                                     SitePredicate filtros) {

        var hierarquia = EHierarquiaSite.getHierarquia(codigoCargo, codigoDepartamento);

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
            .collect(Collectors.toList());
    }

    public List<Integer> getSubordinadosAbaixoDiretor(Integer usuarioId) {
        return usuarioService.getSubordinadosDoUsuario(usuarioId)
            .stream()
            .map(UsuarioSubordinadoDto::getId)
            .collect(Collectors.toList());
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
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SiteSupervisorResponse> getAllSupervisoresBySiteId(Integer id) {
        return findById(id)
            .getSupervisores()
            .stream()
            .map(supervisor -> SiteSupervisorResponse.of(supervisor, buscarCoordenadoresIdsAtivosDoUsuarioId(supervisor.getId())))
            .collect(Collectors.toList());
    }

    public List<SiteSupervisorResponse> getAllSupervisoresByHierarquia(Integer siteId, Integer usuarioSuperiorId) {
        var supervisoresSubordinadosIds =
            usuarioService.getIdsSubordinadosDaHierarquia(usuarioSuperiorId, Set.of(CodigoCargo.SUPERVISOR_OPERACAO.name()));

        return findById(siteId)
            .getSupervisores()
            .stream()
            .filter(supervisor -> supervisoresSubordinadosIds.contains(supervisor.getId()))
            .map(supervisor -> SiteSupervisorResponse.of(supervisor, buscarCoordenadoresIdsAtivosDoUsuarioId(supervisor.getId())))
            .collect(Collectors.toList());
    }

    public Site save(SiteRequest request) {
        validarDadosCadastro(request);
        return siteRepository.save(Site.of(request));
    }

    private void validarAtualizacaoSupervisores(SiteRequest request, Site siteParam) {
        if (!request.isNovoSite()) {
            Optional.ofNullable(siteParam)
                .ifPresent(
                    site -> site.getSupervisores()
                        .stream()
                        .filter(supervisor -> !request.getSupervisoresIds().contains(supervisor.getId()))
                        .forEach(this::verificarSupervisoresEmEquipes)
                );
        }
    }

    private void verificarSupervisoresEmEquipes(Usuario usuario) {
        equipeVendaD2dService.getEquipeVendas(usuario.getId())
            .stream()
            .findFirst()
            .ifPresent(equipeVendaDtos -> {
                throw new ValidacaoException("Para concluir essa operação é necessário"
                    + " inativar a equipe de vendas " + equipeVendaDtos.getDescricao() + ".");
            });
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
        validarAtualizacaoSupervisores(request, site);
        site.update(request);
        return site;
    }

    public void inativar(Integer id) {
        validarInativacao(id);
        var site = findById(id);
        if (Objects.equals(site.getSituacao(), ESituacao.A)) {
            site.inativar();
        }
    }

    public List<SelectResponse> buscarEstadosNaoAtribuidosEmSites(Integer siteExclusoId) {
        var estadosPermitidos = filtrarVisualizacao();
        return Optional.ofNullable(siteExclusoId)
            .map(site -> ufRepository.buscarEstadosNaoAtribuidosEmSitesExcetoPor(estadosPermitidos, site))
            .orElseGet(() -> ufRepository.buscarEstadosNaoAtribuidosEmSites(estadosPermitidos))
            .stream()
            .map(estado -> SelectResponse.of(estado.getId(), estado.getNome()))
            .collect(Collectors.toList());
    }

    public List<SelectResponse> buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(List<Integer> estadosIds,
                                                                               Integer siteIgnoradoId) {
        var cidadesDisponiveis = buscarCidadesDisponiveis(estadosIds, siteIgnoradoId);

        if (!cidadesDisponiveis.isEmpty()) {
            var cidadesResponse = getListaCidadeResponseOrdenadaPorNome(cidadesDisponiveis);
            var distritos = cidadeService.getCidadesDistritos(Eboolean.V);

            return cidadesResponse
                .stream()
                .map(cidadeResponse -> CidadeResponse.definirNomeCidadePaiPorDistritos(cidadeResponse, distritos))
                .map(cidadeResponse -> SelectResponse.of(cidadeResponse.getId(), cidadeResponse.getNomeComCidadePaiEUf()))
                .collect(Collectors.toList());
        }

        return List.of();
    }

    public SiteDetalheResponse getSiteDetalheResponseById(Integer id) {
        var site = findById(id);
        var siteDetalheResponse = SiteDetalheResponse.of(site);
        var cidades = new ArrayList<>(site.getCidades());

        if (!cidades.isEmpty()) {
            var cidadesResponse = definirCidadePai(cidades);
            siteDetalheResponse.setCidades(new HashSet<>(cidadesResponse));
        }

        return siteDetalheResponse;
    }

    private List<CidadeResponse> definirCidadePai(List<Cidade> cidades) {
        var cidadesResponse = getListaCidadeResponseOrdenadaPorNome(cidades);

        if (cidadesResponse.stream().anyMatch(cidadeResponse ->
            hasFkCidadeSemNomeCidadePai(cidadeResponse.getFkCidade(), cidadeResponse.getCidadePai()))) {
            var distritos = cidadeService.getCidadesDistritos(Eboolean.V);

            cidadesResponse
                .forEach(cidadeResponse -> CidadeResponse.definirNomeCidadePaiPorDistritos(cidadeResponse, distritos));
        }

        return cidadesResponse;
    }

    private void validarInativacao(Integer siteId) {
        siteRepository.findById(siteId).ifPresent(
            site -> site.getSupervisores()
                .forEach(this::verificarSupervisoresEmEquipes)
        );
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
                .collect(Collectors.toList())
        );
    }

    private void validarCidadesDisponiveis(SiteRequest siteRequest) {
        var lista = siteRepository.findAll(filtrarPorSituacaoAndCidadesIdInAndIdNot(siteRequest));

        if (!CollectionUtils.isEmpty(lista)) {
            throw EX_CIDADE_VINCULADA_A_OUTRO_SITE;
        }
    }

    private Predicate filtrarPorSituacaoAndCidadesIdInAndIdNot(SiteRequest siteRequest) {
        return new SitePredicate()
            .comSituacao(ESituacao.A)
            .comCidades(siteRequest.getCidadesIds())
            .excetoId(Optional.ofNullable(siteRequest.getId()).orElse(BigInteger.ZERO.intValue()))
            .build();
    }

    private List<Cidade> buscarCidadesDisponiveis(List<Integer> estadosIds, Integer siteIgnoradoId) {
        var filtroVisualizacaoPredicate = filtrarVisualizacao();
        return Optional.ofNullable(siteIgnoradoId)
            .map(id -> cidadeRepository
                .buscarCidadesSemSitesPorEstadosIdsExcetoPor(filtroVisualizacaoPredicate, estadosIds, id))
            .orElseGet(() -> cidadeRepository
                .buscarCidadesVinculadasAoUsuarioSemSite(filtroVisualizacaoPredicate, estadosIds));
    }

    public List<SelectResponse> getSitesByEstadoId(Integer estadoId) {
        return siteRepository.findByEstadoId(estadoId)
            .stream()
            .map(site -> SelectResponse.of(site.getId(), site.getNome()))
            .collect(Collectors.toList());
    }

    public List<SelectResponse> getSitesPorPermissao(Usuario usuario) {
        var sitePredicate = new SitePredicate();
        setFiltrosHierarquia(usuario.getId(), usuario.getCargoCodigo(), usuario.getDepartamentoCodigo(), sitePredicate);
        return siteRepository.findBySituacaoAtiva(sitePredicate.build())
            .stream()
            .map(site -> SelectResponse.of(site.getId(), site.getNome()))
            .collect(Collectors.toList());
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

    public List<SelectResponse> findSitesPermitidosAoUsuarioAutenticado() {
        var usuario = autenticacaoService.getUsuarioAutenticado().getUsuario();
        return getSitesPorPermissao(usuario);
    }

    private Predicate filtrarVisualizacao() {
        var usarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        return new SitePredicate()
            .comFiltroVisualizar(usarioAutenticado)
            .build();
    }

    @SuppressWarnings("LineLength")
    public List<UsuarioSiteResponse> buscarAssistentesAtivosDaHierarquiaDosUsuariosSuperioresIds(List<Integer> usuariosSuperioresIds) {
        return usuarioService
            .buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos(usuariosSuperioresIds, Set.of(CodigoCargo.ASSISTENTE_OPERACAO.name()))
            .stream()
            .map(UsuarioSiteResponse::of)
            .collect(Collectors.toList());
    }

    public List<UsuarioSiteResponse> buscarVendedoresAtivosDaHierarquiaDoUsuarioSuperiorIdSemEquipeVenda(Integer
                                                                                                             usuarioSuperiorId) {
        return equipeVendaD2dService.filtrarUsuariosQuePodemAderirAEquipe(usuarioService
                .buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos(
                    List.of(usuarioSuperiorId), Set.of(CodigoCargo.OPERACAO_TELEVENDAS.name())), null)
            .stream()
            .map(UsuarioSiteResponse::of)
            .collect(Collectors.toList());
    }

    public List<Integer> buscarCoordenadoresIdsAtivosDoUsuarioId(Integer usuarioId) {
        return usuarioService.getSuperioresDoUsuarioPorCargo(usuarioId, CodigoCargo.COORDENADOR_OPERACAO)
            .stream()
            .filter(usuario -> ESituacao.A.getDescricao().toUpperCase().equals(usuario.getStatus()))
            .map(UsuarioHierarquiaResponse::getId)
            .collect(Collectors.toList());
    }

    public SiteCidadeResponse buscarSiteCidadePorCidadeUf(String cidade, String uf) {
        return siteRepository.findSiteCidadeTop1ByPredicate(new CidadePredicate().comNome(cidade).comUf(uf).build()
                .and(new SitePredicate().todosSitesAtivos().build()))
            .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public SiteCidadeResponse buscarSiteCidadePorCodigoCidadeDbm(Integer codigoCidadeDbm) {
        return siteRepository.findSiteCidadeDbmTop1ByPredicate(
            new CidadeDbmPredicate().comCodigoCidadeDbm(codigoCidadeDbm).build()
                .and(new SitePredicate().todosSitesAtivos().build())
        ).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public SiteCidadeResponse buscarSiteCidadePorDdd(Integer ddd) {
        return siteRepository.findSiteDddTop1ByPredicate(
            new CidadeDbmPredicate().comDdd(ddd).build()
                .and(new SitePredicate().todosSitesAtivos().build())
        ).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public List<Site> buscarSitesAtivosPorCoordenadorOuSupervisor(Integer usuarioId) {
        return siteRepository.findAll(
            new SitePredicate()
                .comCoordenadoresOuSupervisor(usuarioId)
                .todosSitesAtivos()
                .build()
        );
    }

    public void gerarRelatorioDiscadorasCsv(SiteFiltros filtros, HttpServletResponse response) {
        var sites = SiteCsvResponse.of(siteRepository.findAllByPredicate(filtros.toPredicate().build()));
        preencherDiscadora(sites);

        var csv = SiteCsvResponse.ofCsv(sites);
        baixarCsv(csv, response);
    }

    private void preencherDiscadora(List<SiteCsvResponse> sites) {
        var discadoras = callService.getDiscadoras();

        sites.forEach(site -> {
            if (Objects.nonNull(site.getDiscadoraId())) {
                discadoras.forEach(discadora -> {
                    if (Objects.equals(site.getDiscadoraId(), discadora.getId())) {
                        site.setDiscadoraText(discadora.getNome());
                    }
                });
            }
        });
    }

    private void baixarCsv(String csv, HttpServletResponse response) {
        if (!CsvUtils.setCsvNoHttpResponse(csv, "csv_ativo_local_proprio_discadora", response)) {
            throw new ValidacaoException("Falha ao tentar baixar relatório.");
        }
    }

    public List<SiteResponse> buscarTodos(SiteFiltros filtros) {
        return siteRepository.findAll(filtros.toPredicate().build())
            .stream()
            .map(SiteResponse::of)
            .collect(Collectors.toList());
    }
}
