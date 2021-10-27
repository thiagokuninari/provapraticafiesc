package br.com.xbrain.autenticacao.modules.site.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.dto.ConfiguracaoTelefoniaResponse;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.site.dto.SiteFiltros;
import br.com.xbrain.autenticacao.modules.site.dto.SiteResponse;
import br.com.xbrain.autenticacao.modules.site.dto.SiteSupervisorResponse;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
import br.com.xbrain.autenticacao.modules.site.repository.SiteRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHierarquiaResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSubordinadoDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CidadePredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoAtivoProprioComCargo;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioResponseHelper.doisUsuarioResponse;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioResponseHelper.umUsuarioResponse;
import static helpers.TestBuilders.*;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SiteServiceTest {

    @InjectMocks
    private SiteService service;
    @Mock
    private SiteRepository siteRepository;
    @Mock
    private UfRepository ufRepository;
    @Mock
    private CidadeRepository cidadeRepository;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private CallService callService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private EquipeVendaD2dService equipeVendaD2dService;

    private void setupSite(Integer idUsuario, Integer idSite, CodigoCargo codigoCargo, String nomeSite, Integer vinculoIndireto) {
        var sitePredicate = umSitePredicateComSupervidorOuCoordenador(idUsuario);
        var pageRequest = umPageRequest();
        var idVinculo = vinculoIndireto != null ? vinculoIndireto : idUsuario;

        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(umUsuarioAutenticadoAtivoProprioComCargo(idVinculo, codigoCargo, CodigoDepartamento.COMERCIAL));
        when(siteRepository.findAll(sitePredicate, pageRequest))
                .thenReturn(new PageImpl<>(umaListaDeSitesVinculadoAUsuarioComCargo(idSite, nomeSite,
                        umUsuario(idUsuario, codigoCargo))));

    }

    @Test
    public void findById_notFoundException_quandoNaoExistirSiteCadastrado() {
        when(siteRepository.findById(any()))
            .thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.findById(1))
            .withMessage("Site não encontrado.");

        verify(siteRepository, atLeastOnce()).findById(eq(1));
    }

    @Test
    public void findById_deveRetornarUmSite_quandoBuscarPorId() {
        when(siteRepository.findById(any()))
            .thenReturn(Optional.of(umSite(1, "Nome", BRT)));

        assertThat(service.findById(1))
            .extracting("id", "nome", "timeZone")
            .containsExactly(1, "Nome", BRT);

        verify(siteRepository, atLeastOnce()).findById(eq(1));
    }

    @Test
    public void getAllByUsuarioLogado_deveRetornarSelectResponseComSites_quandoExistirParaOUsuario() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelBackoffice());

        when(siteRepository.findAll(any(Predicate.class)))
            .thenReturn(umaListaSites());

        assertThat(service.getAllByUsuarioLogado())
            .hasSize(3)
            .extracting("value", "label")
            .containsExactly(
                tuple(1, "Site Brandon Big"),
                tuple(2, "Site Dinossauro do Acre"),
                tuple(3, "Site Amazonia Queimada")
            );
    }

    @Test
    public void getAll_deveRetornarTodosOsSiltes_quandoNivelMso() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoAtivoProprioComCargo(101, MSO_CONSULTOR, CodigoDepartamento.COMERCIAL));
        when(siteRepository.findAll(any(Predicate.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(umaListaSites()));

        assertThat(service.getAll(new SiteFiltros(), new PageRequest()))
            .hasSize(3)
            .extracting("id", "nome", "timeZone")
            .containsExactly(
                tuple(1, "Site Brandon Big", BRT),
                tuple(2, "Site Dinossauro do Acre", ACT),
                tuple(3, "Site Amazonia Queimada", AMT)
            );
    }

    @Test
    public void save_validacaoException_quandoExistirUmSiteComOMesmoNome() {
        when(siteRepository.findAll()).thenReturn(umaListaSites());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(umSiteRequest()))
            .withMessage("Site já cadastrado anteriormente com esse nome.");

        verify(siteRepository, atLeastOnce()).findAll();
        verifyZeroInteractions(cidadeRepository);
    }

    @Test
    public void save_validacaoException_quandoCidadesJaExistentesEmOutroSite() {
        when(siteRepository.findAll(umSitePredicate())).thenReturn(List.of(umSite(1, "Brandin", BRT)));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(umSiteRequest()))
            .withMessage("Existem cidades vinculadas à outro site.");

        verify(siteRepository, atLeastOnce()).findAll(umSitePredicate());
        verifyZeroInteractions(cidadeRepository);
    }

    @Test
    public void update_deveAtualizarAsInformacoesDeUmSiteExistente() {
        when(siteRepository.findById(anyInt()))
            .thenReturn(Optional.of(umSiteComSupervisores()));

        var request = umSiteRequest();
        request.setId(1);
        request.setSupervisoresIds(List.of(100, 110, 112));

        assertThat(service.update(request))
            .extracting("id", "timeZone")
            .contains(1, BRT);

        verify(siteRepository, never()).save(any(Site.class));
        verify(siteRepository, atLeastOnce()).findById(eq(1));
    }

    @Test
    public void buscarEstadosNaoAtribuidosEmSites_deveRetornarOsEstados_quandoBuscadoEmTodosOsSites() {
        when(ufRepository.buscarEstadosNaoAtribuidosEmSites(any()))
            .thenReturn(umaListaUfs());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());

        assertThat(service.buscarEstadosNaoAtribuidosEmSites(null))
            .extracting("value", "label")
            .containsExactly(
                tuple(1, "UF 1"),
                tuple(2, "UF 2"),
                tuple(3, "UF 3")
            );
        verify(ufRepository, never()).buscarEstadosNaoAtribuidosEmSitesExcetoPor(any(), any());
    }

    @Test
    public void buscarEstadosNaoAtribuidosEmSites_deveRetornarOsEstados_quandoBuscadoEmTodosOsSitesExcetoPeloAtual() {
        when(ufRepository.buscarEstadosNaoAtribuidosEmSitesExcetoPor(any(), any()))
            .thenReturn(umaListaUfs());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());

        assertThat(service.buscarEstadosNaoAtribuidosEmSites(1))
            .extracting("value", "label")
            .containsExactly(
                tuple(1, "UF 1"),
                tuple(2, "UF 2"),
                tuple(3, "UF 3")
            );
    }

    @Test
    public void buscarCidadesNaoAtribuidasEmSitesPorEstadosIds_deveRetornarAsCidades_quandoBuscadoEmTodosOsSites() {
        when(cidadeRepository.buscarCidadesVinculadasAoUsuarioSemSite(any(), anyList()))
            .thenReturn(umListaCidades());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());

        assertThat(service.buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(List.of(1, 2), null))
            .extracting("value", "label")
            .containsExactlyInAnyOrder(
                tuple(1, "CIDADE 1 - PR"),
                tuple(2, "CIDADE 2 - SP")
            );

        verify(cidadeRepository, never()).buscarCidadesSemSitesPorEstadosIdsExcetoPor(any(), anyList(), any());
        verify(cidadeRepository, atLeastOnce()).buscarCidadesVinculadasAoUsuarioSemSite(any(),
            argThat(arg -> arg.size() == 2 && arg.containsAll(List.of(1, 2))));
    }

    @Test
    public void buscarCidadesNaoAtribuidasEmSitesPorEstadosIds_deveReotnarAsCidades_quandoBuscarOsSitesIgnorandoOAtual() {
        when(cidadeRepository.buscarCidadesSemSitesPorEstadosIdsExcetoPor(any(), anyList(), any()))
            .thenReturn(umListaCidades());
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado());
        assertThat(service.buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(List.of(1, 2), 1))
            .extracting("value", "label")
            .containsExactlyInAnyOrder(
                tuple(1, "CIDADE 1 - PR"),
                tuple(2, "CIDADE 2 - SP")
            );

        verify(cidadeRepository, atLeastOnce()).buscarCidadesSemSitesPorEstadosIdsExcetoPor(any(),
            argThat(arg -> arg.size() == 2 && arg.containsAll(List.of(1, 2))), eq(1));
        verify(cidadeRepository, never()).buscarCidadesVinculadasAoUsuarioSemSite(any(), any());
    }

    @Test
    public void getAllAtivos_listaComTresSites_quandoBuscarSitesAtivos() {
        when(siteRepository.findBySituacaoAtiva(new SitePredicate().build()))
            .thenReturn(umaListaSites());

        assertThat(service.getAllAtivos(new SiteFiltros()))
            .extracting("value", "label")
            .containsExactly(
                tuple(1, "Site Brandon Big"),
                tuple(2, "Site Dinossauro do Acre"),
                tuple(3, "Site Amazonia Queimada")
            );
    }

    @Test
    public void getAllSupervisoresBySiteId_umaListaComDoisSupervisores_quandoBuscarSupervisoresPeloSiteId() {
        var site = umSite(1, "TESTE SITE", BRT);
        site.setSupervisores(umaListaSupervisores());

        when(siteRepository.findById(1))
            .thenReturn(Optional.of(site));
        when(usuarioService.getSuperioresDoUsuarioPorCargo(eq(1), eq(COORDENADOR_OPERACAO)))
            .thenReturn(List.of(UsuarioHierarquiaResponse.builder().id(100).status("ATIVO").build()));

        assertThat(service.getAllSupervisoresBySiteId(1))
            .extracting("id", "nome", "situacao", "coordenadoresIds")
            .containsExactlyInAnyOrder(
                tuple(1, "RENATO", ESituacao.A, List.of(100)),
                tuple(2, "MARIA", ESituacao.I, List.of()),
                tuple(3, "JOAO", ESituacao.R, List.of()));
    }

    @Test
    public void getSitesByEstadoId_umaListaComTresSites_quandoBuscarSitesPeloEstadoId() {
        when(siteRepository.findByEstadoId(1))
            .thenReturn(umaListaSites());

        assertThat(service.getSitesByEstadoId(1))
            .extracting("value", "label")
            .containsExactly(
                tuple(1, "Site Brandon Big"),
                tuple(2, "Site Dinossauro do Acre"),
                tuple(3, "Site Amazonia Queimada")
            );
    }

    @Test
    public void removerDiscadora_void_quandoSitePossuirDiscadora() {
        var site = umSite(1, "brandon city", BRT);
        site.setDiscadoraId(2);
        when(siteRepository.findById(eq(1))).thenReturn(Optional.of(site));

        service.removerDiscadora(1);

        verify(siteRepository, atLeastOnce()).removeDiscadoraBySite(eq(1));
        verify(callService, atLeastOnce()).cleanCacheableSiteAtivoProprio();
        verify(callService, atLeastOnce()).desvincularRamaisDaDiscadoraAtivoProprio(eq(1), eq(2));
    }

    @Test
    public void adicionarDiscadora_void_quandoSiteNaoPossuirDiscadora() {
        service.adicionarDiscadora(1, List.of(1, 2, 3));

        verify(siteRepository, atLeastOnce()).updateDiscadoraBySites(eq(1), eq(List.of(1, 2, 3)));
        verify(callService, atLeastOnce()).cleanCacheableSiteAtivoProprio();
    }

    @Test
    public void getSiteVinculadoAoSupervidor_deveRetornarSitesVinculadosAoProprioSupervidor_quandoSiteVinculadoAoSupervisor() {
        setupSite(100, 10, SUPERVISOR_OPERACAO, "Site", null);
        assertThat(service.getAll(new SiteFiltros(), new PageRequest()))
            .extracting("id", "nome", "timeZone")
            .containsExactly(
                tuple(10, "Site", BRT)
            );
    }

    @Test
    public void getSiteVinculoCoordenador_deveRetornarSitesVinculadosAoProprioCoordenador_quandoExistirSiteVinculado() {
        setupSite(101, 11, COORDENADOR_OPERACAO, "SITE_VINCULADO_AO_COORDENADOR", null);
        assertThat(service.getAll(new SiteFiltros(), new PageRequest()))
            .extracting("id", "nome", "timeZone")
            .containsExactly(
                tuple(11, "SITE_VINCULADO_AO_COORDENADOR", BRT)
            );
    }

    @Test
    public void getSiteAbaixoDoDiretor_deveRetornarSitesVinculadosAoCoordenadorAbaixoDoDiretor_quandoExistirSitesVinculados() {
        setupSite(101, 11, DIRETOR_OPERACAO, "SITE_VINCULADO_AO_COORDENADOR_DIRETOR", 102);
        when(usuarioService.getSubordinadosDoUsuario(102))
            .thenReturn(singletonList(usuarioSubordinadoDtoDtoResponse(101, COORDENADOR_OPERACAO)));

        assertThat(service.getAll(new SiteFiltros(), new PageRequest()))
            .extracting("id", "nome", "timeZone")
            .containsExactly(
                tuple(11, "SITE_VINCULADO_AO_COORDENADOR_DIRETOR", BRT)
            );
    }

    @Test
    public void getSiteAbaixoDoDiretor_deveRetornarSiteVinculadoAoSupervisorAbaixoDoDiretor_quandoExistirSitesVinculados() {
        setupSite(101, 11, DIRETOR_OPERACAO, "SITE_VINCULADO_AO_SUPERVISOR_DIRETOR", 102);
        when(usuarioService.getSubordinadosDoUsuario(102))
            .thenReturn(singletonList(usuarioSubordinadoDtoDtoResponse(101, SUPERVISOR_OPERACAO)));

        assertThat(service.getAll(new SiteFiltros(), new PageRequest()))
            .extracting("id", "nome", "timeZone")
            .containsExactly(
                tuple(11, "SITE_VINCULADO_AO_SUPERVISOR_DIRETOR", BRT)
            );
    }

    @Test
    public void getSiteAbaixoDoGerente_deveRetornarSiteVinculadoAoSupervisorAbaixoDoDiretor_quandoExistirSitesVinculados() {
        setupSite(101, 11, GERENTE_OPERACAO, "SITE_VINCULADO_AO_GERENTE_DIRETOR", 102);
        when(usuarioService.getSubordinadosDoUsuario(102))
            .thenReturn(singletonList(usuarioSubordinadoDtoDtoResponse(101, SUPERVISOR_OPERACAO)));

        assertThat(service.getAll(new SiteFiltros(), new PageRequest()))
            .extracting("id", "nome", "timeZone")
            .containsExactly(
                tuple(11, "SITE_VINCULADO_AO_GERENTE_DIRETOR", BRT)
            );
    }

    @Test
    public void getListSites_deveRetornarListSites_quandoDiretorOuGerentePossuirDiferentesColaboradoresComDiretentesSites() {
        var listSiteComUsuarioVinculado = List.of(
            umSiteVinculado(8, "SITE_COORDENADOR", umUsuario(110, COORDENADOR_OPERACAO)),
            umSiteVinculado(9, "SITE_SUPERVISOR", umUsuario(111, SUPERVISOR_OPERACAO)));
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoAtivoProprioComCargo(100, DIRETOR_OPERACAO, CodigoDepartamento.COMERCIAL));
        when(siteRepository.findAll(umSitePredicateComSupervidoresOuCoordenadores(List.of(110, 111)),
            umPageRequest()))
            .thenReturn(new PageImpl<>(listSiteComUsuarioVinculado));
        when(usuarioService.getSubordinadosDoUsuario(100))
            .thenReturn(List.of(
                usuarioSubordinadoDtoDtoResponse(110, SUPERVISOR_OPERACAO),
                usuarioSubordinadoDtoDtoResponse(111, SUPERVISOR_OPERACAO)));
        assertThat(service.getAll(new SiteFiltros(), new PageRequest()))
            .extracting("id", "nome", "timeZone")
            .containsExactly(
                tuple(8, "SITE_COORDENADOR", BRT),
                tuple(9, "SITE_SUPERVISOR", BRT)
            );
    }

    @Test
    public void retornaVazio_deveRetornarVazio_quandoExistirSitesCadastradosEDiretorPossuirSubordinadosSemSites() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoAtivoProprioComCargo(100, DIRETOR_OPERACAO, CodigoDepartamento.COMERCIAL));
        when(usuarioService.getSubordinadosDoUsuario(100))
            .thenReturn(List.of(
                usuarioSubordinadoDtoDtoResponse(110, SUPERVISOR_OPERACAO),
                usuarioSubordinadoDtoDtoResponse(111, SUPERVISOR_OPERACAO)));
        assertNull(service.getAll(new SiteFiltros(), new PageRequest()));
    }

    @Test
    public void getAllSupervisoresByHierarquia_listaSupervisores_quandoForDoSiteIdESubordinadoDoUsuarioSuperiorIdInformado() {
        when(usuarioService.getIdsSubordinadosDaHierarquia(200, SUPERVISOR_OPERACAO.name()))
            .thenReturn(List.of(110, 112));
        when(siteRepository.findById(100))
            .thenReturn(Optional.of(umSiteComSupervisores()));
        when(usuarioService.getSuperioresDoUsuarioPorCargo(eq(110), eq(COORDENADOR_OPERACAO)))
            .thenReturn(List.of(UsuarioHierarquiaResponse.builder().id(100).status("ATIVO").build()));

        var actual = service.getAllSupervisoresByHierarquia(100, 200);

        var expected = List.of(
            SiteSupervisorResponse.builder()
                .id(110)
                .nome("JOAO")
                .situacao(ESituacao.A)
                .coordenadoresIds(List.of(100))
                .build(),
            SiteSupervisorResponse.builder()
                .id(112)
                .nome("CARLOS")
                .situacao(ESituacao.I)
                .coordenadoresIds(List.of())
                .build());
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void getSiteBySupervisorId_siteSp_quandoBuscarSitePeloSupervisorId() {
        when(siteRepository.findBySupervisorId(100))
            .thenReturn(umSite(100, "SITE SP", BRT));

        assertThat(service.getSiteBySupervisorId(100))
            .isInstanceOf(SiteResponse.class)
            .extracting("id", "nome")
            .containsExactly(100, "SITE SP");
    }

    @Test
    public void exceptionInativar_deveLancarException_quandoInativarSiteComSupervisorVinculadoAEquipe() {
        var site = umSiteVinculado(8, "site", umUsuario(110, SUPERVISOR_OPERACAO));
        when(siteRepository.findById(any())).thenReturn(Optional.of(site));
        when(equipeVendaD2dService.getEquipeVendas(any())).thenReturn(umaListEquipeResponse());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.inativar(12))
            .withMessage("Para concluir essa operação é necessário remover o supervisor(a) "
                + "UM USUARIO SUPERVISOR_OPERACAO da equipe de vendas Equipe ativo.");
    }

    @Test
    public void buscarAssistentesAtivosDaHierarquiaDosUsuariosSuperioresIds_usuarioResponse_seSolicitado() {
        when(usuarioService
                .buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos(eq(List.of(1)), eq(Set.of(ASSISTENTE_OPERACAO.name()))))
            .thenReturn(List.of(
                umUsuarioResponse(1, "NOME 1", ESituacao.A, ASSISTENTE_OPERACAO),
                umUsuarioResponse(2, "NOME 2", ESituacao.A, ASSISTENTE_OPERACAO),
                umUsuarioResponse(3, "NOME 3", ESituacao.A, ASSISTENTE_OPERACAO)));

        assertThat(service.buscarAssistentesAtivosDaHierarquiaDosUsuariosSuperioresIds(List.of(1)))
            .extracting("usuarioId", "usuarioNome", "cargoNome")
            .containsExactly(
                tuple(1, "NOME 1", "ASSISTENTE_OPERACAO"),
                tuple(2, "NOME 2", "ASSISTENTE_OPERACAO"),
                tuple(3, "NOME 3", "ASSISTENTE_OPERACAO"));
    }

    @Test
    public void buscarVendedoresAtivosDaHierarquiaDoUsuarioSuperiorIdSemEquipeVenda_usuarioResponse_seSolicitado() {
        var umaListaUsuarioResponse = List.of(
            doisUsuarioResponse(1, "NOME 1", "OPERACAO TELEVENDAS", OPERACAO_TELEVENDAS),
            doisUsuarioResponse(2, "NOME 2", "OPERACAO TELEVENDAS", OPERACAO_TELEVENDAS),
            doisUsuarioResponse(3, "NOME 3", "OPERACAO TELEVENDAS", OPERACAO_TELEVENDAS));

        when(usuarioService.buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos(
                eq(List.of(1)), eq(Set.of(OPERACAO_TELEVENDAS.name()))))
            .thenReturn(umaListaUsuarioResponse);
        when(equipeVendaD2dService.filtrarUsuariosQuePodemAderirAEquipe(eq(umaListaUsuarioResponse), eq(null)))
            .thenReturn(umaListaUsuarioResponse);

        assertThat(service.buscarVendedoresAtivosDaHierarquiaDoUsuarioSuperiorIdSemEquipeVenda(1))
            .extracting("usuarioId", "usuarioNome", "cargoNome")
            .containsExactly(
                tuple(1, "NOME 1", "OPERACAO_TELEVENDAS"),
                tuple(2, "NOME 2", "OPERACAO_TELEVENDAS"),
                tuple(3, "NOME 3", "OPERACAO_TELEVENDAS"));
    }

    @Test
    public void buscarCoordenadoresIdsAtivosDoUsuarioId_listaVazia_seNaoHouverCoordenadoresAtivos() {
        when(usuarioService.getSuperioresDoUsuarioPorCargo(eq(1), eq(COORDENADOR_OPERACAO)))
            .thenReturn(List.of(UsuarioHierarquiaResponse.builder().id(100).status("INATIVO").build()));

        assertThat(service.buscarCoordenadoresIdsAtivosDoUsuarioId(1))
            .isEmpty();
    }

    @Test
    public void buscarCoordenadoresIdsAtivosDoUsuarioId_listaDeInteiros_seSolicitado() {
        when(usuarioService.getSuperioresDoUsuarioPorCargo(eq(1), eq(COORDENADOR_OPERACAO)))
            .thenReturn(List.of(UsuarioHierarquiaResponse.builder().id(100).status("ATIVO").build()));

        assertThat(service.buscarCoordenadoresIdsAtivosDoUsuarioId(1))
            .isEqualTo(List.of(100));
    }

    @Test
    public void buscarSiteCidadePorCidadeUf_notFoundException_seNaoHouverResultados() {
        var predicate = new CidadePredicate().comNome("LONDRINA").comUf("PR").build()
            .and(new SitePredicate().todosSitesAtivos().build());

        when(siteRepository.findSiteCidadeTop1ByPredicate(eq(predicate)))
            .thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.buscarSiteCidadePorCidadeUf("LONDRINA", "PR"))
            .withMessage("Site não encontrado.");
    }

    @Test
    public void buscarSiteCidadePorCidadeUf_siteCidadeResponse_seNaoHouverResultados() {
        var predicate = new CidadePredicate().comNome("LONDRINA").comUf("PR").build()
            .and(new SitePredicate().todosSitesAtivos().build());

        when(siteRepository.findSiteCidadeTop1ByPredicate(eq(predicate)))
            .thenReturn(Optional.of(umSiteCidade()));

        assertThat(service.buscarSiteCidadePorCidadeUf("LONDRINA", "PR"))
            .extracting("siteId", "siteNome", "cidadeId", "cidadeNome", "ufId", "ufNome")
            .containsExactly(1, "SITE 1", 1, "LONDRINA", 1, "PR");
    }

    @Test
    public void buscarSiteCidadePorCodigoCidadeDbm_notFoundException_seNaoHouverResultados() {
        var predicate = new CidadePredicate().comCodigoCidadeDbm(1).build()
            .and(new SitePredicate().todosSitesAtivos().build());

        when(siteRepository.findSiteCidadeTop1ByPredicate(eq(predicate)))
            .thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.buscarSiteCidadePorCodigoCidadeDbm(1))
            .withMessage("Site não encontrado.");
    }

    @Test
    public void buscarSiteCidadePorCodigoCidadeDbm_siteCidadeResponse_seNaoHouverResultados() {
        var predicate = new CidadePredicate().comCodigoCidadeDbm(1).build()
            .and(new SitePredicate().todosSitesAtivos().build());

        when(siteRepository.findSiteCidadeTop1ByPredicate(eq(predicate)))
            .thenReturn(Optional.of(umSiteCidade()));

        assertThat(service.buscarSiteCidadePorCodigoCidadeDbm(1))
            .extracting("siteId", "siteNome", "cidadeId", "cidadeNome", "ufId", "ufNome")
            .containsExactly(1, "SITE 1", 1, "LONDRINA", 1, "PR");
    }

    @Test
    public void buscarSitesAtivosPorCoordenadorOuSupervisor_deveChamarRepository_seSolicitado() {
        var predicate = new SitePredicate()
            .comCoordenadoresOuSupervisor(1)
            .todosSitesAtivos()
            .build();

        service.buscarSitesAtivosPorCoordenadorOuSupervisor(1);

        verify(siteRepository, times(1)).findAll(eq(predicate));
    }

    @Test
    @SneakyThrows
    public void gerarRelatorioCsv_deveGerarRelatorioCsv_seDadosForemCorretos() {
        var filtros = new SiteFiltros();
        var response = new MockHttpServletResponse();

        when(siteRepository.findAllByPredicate(filtros.toPredicate().build())).thenReturn(umaListaDeReagendamentoConfiguracao());
        when(callService.getDiscadoras()).thenReturn(umaListaConfiguracaoTelefoniaResponse());

        service.gerarRelatorioDiscadorasCsv(filtros, response);

        verify(siteRepository, times(1)).findAllByPredicate(eq(filtros.toPredicate().build()));
        verify(callService, times(1)).getDiscadoras();
    }

    @Test
    public void buscarTodos_deveRetornarListaDeSiteResponse_seSolicitado() {
        when(siteRepository.findAll(eq(new SitePredicate().build())))
            .thenReturn(List.of(
                Site
                    .builder()
                    .id(1)
                    .nome("SITE NOME")
                    .timeZone(BRT)
                    .situacao(ESituacao.A)
                    .coordenadores(Set.of(Usuario.builder().id(1).build()))
                    .supervisores(Set.of(Usuario.builder().id(1).build()))
                    .estados(Set.of(Uf.builder().id(1).build()))
                    .cidades(Set.of(Cidade.builder().id(1).build()))
                    .discadoraId(1)
                    .siteNacional(Eboolean.F)
                    .build()
            ));

        var atual = service.buscarTodos(new SiteFiltros());
        var esperado = SiteResponse
            .builder()
            .id(1)
            .nome("SITE NOME")
            .timeZone(BRT)
            .situacao(ESituacao.A)
            .discadoraId(1)
            .siteNacional(false)
            .build();

        assertThat(atual)
            .usingElementComparatorOnFields("id", "nome", "timeZone", "situacao", "coordenadoresIds", "supervisoresIds",
                "estadosIds", "cidadesIds", "discadoraId", "siteNacional")
            .isEqualTo(List.of(esperado));
    }

    private Site umReagendamentoConfiguracaoResponse(Integer id) {
        return Site.builder()
            .id(id)
            .nome("Razao Social")
            .discadoraId(id)
            .build();
    }

    private List<Site> umaListaDeReagendamentoConfiguracao() {
        return List.of(
            umReagendamentoConfiguracaoResponse(1),
            umReagendamentoConfiguracaoResponse(2),
            umReagendamentoConfiguracaoResponse(3),
            umReagendamentoConfiguracaoResponse(4),
            umReagendamentoConfiguracaoResponse(5),
            umReagendamentoConfiguracaoResponse(6)
        );
    }

    private ConfiguracaoTelefoniaResponse umaListaConfiguracaoTelefoniaResponse(Integer id, String nome) {
        return ConfiguracaoTelefoniaResponse.builder()
            .id(id)
            .nome(nome)
            .build();
    }

    private List<ConfiguracaoTelefoniaResponse> umaListaConfiguracaoTelefoniaResponse() {
        return List.of(
            umaListaConfiguracaoTelefoniaResponse(1, "Discadora 1"),
            umaListaConfiguracaoTelefoniaResponse(2, "Discadora 2"),
            umaListaConfiguracaoTelefoniaResponse(3, "Discadora 3"),
            umaListaConfiguracaoTelefoniaResponse(4, "Discadora 4"),
            umaListaConfiguracaoTelefoniaResponse(5, "Discadora 5"),
            umaListaConfiguracaoTelefoniaResponse(6, "Discadora 6")
        );
    }

    private Predicate umSitePredicate() {
        return new SitePredicate()
            .comSituacao(ESituacao.A)
            .comCidades(List.of(1, 10))
            .excetoId(0)
            .build();
    }

    public List<EquipeVendaDto> umaListEquipeResponse() {
        return List.of(EquipeVendaDto.builder()
        .id(10)
        .descricao("Equipe ativo")
        .build());
    }

    private Predicate umSitePredicateComSupervidoresOuCoordenadores(List<Integer> id) {
        return new SitePredicate()
            .comCoordenadoresOuSupervisores(id)
            .build();
    }

    private Site umSiteComSupervisores() {
        return Site.builder()
            .id(100)
            .nome("SITE SP")
            .situacao(ESituacao.A)
            .supervisores(
                Set.of(
                    Usuario.builder()
                        .id(100)
                        .nome("RENATO")
                        .situacao(ESituacao.R)
                        .build(),
                    Usuario.builder()
                        .id(110)
                        .nome("JOAO")
                        .situacao(ESituacao.A)
                        .build(),
                    Usuario.builder()
                        .id(112)
                        .nome("CARLOS")
                        .situacao(ESituacao.I)
                        .build()))
            .build();
    }

    public UsuarioSubordinadoDto usuarioSubordinadoDtoDtoResponse(Integer id, CodigoCargo codigoCargo) {
        return UsuarioSubordinadoDto.builder()
            .id(id)
            .codigoCargo(codigoCargo)
            .build();
    }

    private PageRequest umPageRequest() {
        return new PageRequest();
    }

    private Predicate umSitePredicateComSupervidorOuCoordenador(Integer id) {
        return new SitePredicate()
            .comCoordenadoresOuSupervisores(singletonList(id))
            .build();
    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        return umUsuarioAutenticadoAtivoProprioComCargo(1, COORDENADOR_OPERACAO,
            CodigoDepartamento.COMERCIAL);
    }
}
