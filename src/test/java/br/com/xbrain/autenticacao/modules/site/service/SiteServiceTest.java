package br.com.xbrain.autenticacao.modules.site.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.site.dto.SiteFiltros;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
import br.com.xbrain.autenticacao.modules.site.repository.SiteRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import com.querydsl.core.types.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice;
import static helpers.TestBuilders.*;
import static org.assertj.core.api.Assertions.*;
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
    public void getAll_deveRetornarUmaPaginaDeSites_quandoFiltrar() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelBackoffice());

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
            .withMessage("Site já cadastrado no sistema.");

        verify(siteRepository, atLeastOnce()).findAll();
        verifyZeroInteractions(cidadeRepository);
    }

    @Test
    public void save_validacaoException_quandoCidadesJaExistentesEmOutroSite() {
        when(siteRepository.findAll()).thenReturn(List.of());
        when(siteRepository.findFirstByCidadesIdInAndIdNot(anyList(), any()))
            .thenReturn(Optional.of(umSite(1, "Brandin", BRT)));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(umSiteRequest()))
            .withMessage("Existem cidades vinculadas à outro site.");

        verify(siteRepository, atLeastOnce()).findAll();
        verify(siteRepository, atLeastOnce()).findFirstByCidadesIdInAndIdNot(argThat(arg -> arg.size() == 2), eq(0));
        verifyZeroInteractions(cidadeRepository);
    }

    @Test
    public void save_deveSalvarIncluindoCidadesDisponiveis_quandoFlagForIncluirCidadesDisponiveis() {
        when(siteRepository.findAll()).thenReturn(List.of());
        when(cidadeRepository.buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(anyList()))
            .thenReturn(umListaCidades());

        var request = umSiteRequest();
        request.setIncluirCidadesDisponiveis(true);

        service.save(request);

        assertThat(request.getCidadesIds())
            .contains(1, 2);

        verify(siteRepository, atLeastOnce()).findAll();
        verify(siteRepository, atLeastOnce()).save(any(Site.class));
        verify(siteRepository, never()).findFirstByCidadesIdInAndIdNot(any(), any());
    }

    @Test
    public void update_deveAtualizarAsInformacoesDeUmSiteExistente() {
        when(siteRepository.findById(anyInt()))
            .thenReturn(Optional.of(umSite(1, "Sitezão", AMT)));

        var request = umSiteRequest();
        request.setId(1);

        assertThat(service.update(request))
            .extracting("id", "nome", "timeZone")
            .containsExactly(1, "Site brandon big", BRT);

        verify(siteRepository, never()).save(any(Site.class));
        verify(siteRepository, atLeastOnce()).findById(eq(1));
    }

    @Test
    public void ativarOuInativar_deveAlterarASituacaoDeUmSite() {
        var site = umSite(1, "Sitezão", AMT);
        site.setSituacao(ESituacao.A);

        when(siteRepository.findById(anyInt()))
            .thenReturn(Optional.of(site));

        service.inativar(1);
        assertThat(site.getSituacao()).isEqualTo(ESituacao.I);

        service.ativar(1);
        assertThat(site.getSituacao()).isEqualTo(ESituacao.A);

        verify(siteRepository, atLeastOnce()).findById(eq(1));
    }

    @Test
    public void buscarEstadosNaoAtribuidosEmSites_deveRetornarOsEstados_quandoBuscadoEmTodosOsSites() {
        when(ufRepository.buscarEstadosNaoAtribuidosEmSites())
            .thenReturn(umaListaUfs());

        assertThat(service.buscarEstadosNaoAtribuidosEmSites(null))
            .extracting("value", "label")
            .containsExactly(
                tuple(1, "UF 1"),
                tuple(2, "UF 2"),
                tuple(3, "UF 3")
            );

        verify(ufRepository, atLeastOnce()).buscarEstadosNaoAtribuidosEmSites();
        verify(ufRepository, never()).buscarEstadosNaoAtribuidosEmSitesExcetoPor(any());
    }

    @Test
    public void buscarEstadosNaoAtribuidosEmSites_deveRetornarOsEstados_quandoBuscadoEmTodosOsSitesExcetoPeloAtual() {
        when(ufRepository.buscarEstadosNaoAtribuidosEmSitesExcetoPor(any()))
            .thenReturn(umaListaUfs());

        assertThat(service.buscarEstadosNaoAtribuidosEmSites(1))
            .extracting("value", "label")
            .containsExactly(
                tuple(1, "UF 1"),
                tuple(2, "UF 2"),
                tuple(3, "UF 3")
            );

        verify(ufRepository, never()).buscarEstadosNaoAtribuidosEmSites();
        verify(ufRepository, atLeastOnce()).buscarEstadosNaoAtribuidosEmSitesExcetoPor(eq(1));
    }

    @Test
    public void buscarCidadesNaoAtribuidasEmSitesPorEstadosIds_deveRetornarAsCidades_quandoBuscadoEmTodosOsSites() {
        when(cidadeRepository.buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(anyList()))
            .thenReturn(umListaCidades());

        assertThat(service.buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(List.of(1, 2), null))
            .extracting("value", "label")
            .containsExactlyInAnyOrder(
                tuple(1, "CIDADE 1 - PR"),
                tuple(2, "CIDADE 2 - SP")
            );

        verify(cidadeRepository, never()).buscarCidadesNaoAtribuidasEmSitesPorEstadosIdsExcetoPor(anyList(), any());
        verify(cidadeRepository, atLeastOnce()).buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(
            argThat(arg -> arg.size() == 2 && arg.containsAll(List.of(1, 2))));
    }

    @Test
    public void buscarCidadesNaoAtribuidasEmSitesPorEstadosIds_deveReotnarAsCidades_quandoBuscarOsSitesIgnorandoOAtual() {
        when(cidadeRepository.buscarCidadesNaoAtribuidasEmSitesPorEstadosIdsExcetoPor(anyList(), any()))
            .thenReturn(umListaCidades());

        assertThat(service.buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(List.of(1, 2), 1))
            .extracting("value", "label")
            .containsExactlyInAnyOrder(
                tuple(1, "CIDADE 1 - PR"),
                tuple(2, "CIDADE 2 - SP")
            );

        verify(cidadeRepository, atLeastOnce()).buscarCidadesNaoAtribuidasEmSitesPorEstadosIdsExcetoPor(
            argThat(arg -> arg.size() == 2 && arg.containsAll(List.of(1, 2))), eq(1));
        verify(cidadeRepository, never()).buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(any());
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

        assertThat(service.getAllSupervisoresBySiteId(1))
            .extracting("id", "nome")
            .containsExactlyInAnyOrder(
                tuple(1, "RENATO"),
                tuple(2, "MARIA")
            );
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

}
