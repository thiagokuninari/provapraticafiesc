package br.com.xbrain.autenticacao.modules.site.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.predicate.SitePredicate;
import br.com.xbrain.autenticacao.modules.site.repository.SiteRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioSiteService;
import org.assertj.core.groups.Tuple;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.buildUsuario;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelMso;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioSiteServiceTest {

    @InjectMocks
    private UsuarioSiteService usuarioSiteService;
    @Mock
    private SiteService siteService;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private SiteRepository siteRepository;
    @Mock
    private EquipeVendaD2dService equipeVendaD2dService;

    @Test
    public void buscarCoordenadores_deveRetornarCoordendoresDaHierarquiaDoUsuarioLogado_quandoSolicitarPorCidade() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoPadrao());

        when(usuarioRepository.findCoordenadoresDisponiveis(new SitePredicate().build())).thenReturn(umaListaDeCoordenadores());

        when(usuarioRepository.findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(10, CodigoCargo.COORDENADOR_OPERACAO))
            .thenReturn(List.of(UsuarioNomeResponse.builder()
                .id(11125)
                .build()));

        var coordenadoresDisponiveis = usuarioSiteService.buscarCoordenadoresDisponiveis();

        assertThat(coordenadoresDisponiveis)
            .extracting(UsuarioNomeResponse::getId, UsuarioNomeResponse::getNome)
            .containsExactly(Tuple.tuple(11125, "Coordenador2 operacao ativo local"));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void buscarCoordenadoresDisponiveis_deveRetornarCoordenadoresDisponiveisEIgnoraSiteInativo_quandoSolicitarPorCidade() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoPadrao());

        when(usuarioRepository.findCoordenadoresDisponiveis(new SitePredicate().build())).thenReturn(umaListaDeCoordenadores());

        when(usuarioRepository
            .findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(umUsuarioAutenticadoPadrao().getId(), CodigoCargo.COORDENADOR_OPERACAO))
            .thenReturn(List.of(umUsuarioNomeResponse(11125), umUsuarioNomeResponse(11126)));

        var coordenadoresDisponiveis = usuarioSiteService.buscarCoordenadoresDisponiveis();
        assertThat(coordenadoresDisponiveis)
            .extracting(UsuarioNomeResponse::getId, UsuarioNomeResponse::getNome)
            .containsExactly(
                tuple(11125, "Coordenador2 operacao ativo local"),
                tuple(11126, "Coordenador sem site operacao ativo local"));
    }

    @Test
    public void buscarCoordenadoresDisponiveis_deveRetornarTodosOsCoordenadoresDisponiveisPorCidade_quandoUsuarioLogadoForMso() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoNivelMso());

        when(usuarioRepository.findCoordenadoresDisponiveis(new SitePredicate().build())).thenReturn(umaListaDeCoordenadores());

        assertThat(usuarioSiteService.buscarCoordenadoresDisponiveis())
            .extracting(UsuarioNomeResponse::getId, UsuarioNomeResponse::getNome)
            .containsExactly(
                tuple(11125, "Coordenador2 operacao ativo local"),
                tuple(11126, "Coordenador sem site operacao ativo local"));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void buscarCoordenadoresDisponiveisEVinculadosAoSite_deveRetornarCoordenadoresDisponiveisENoSiteSendoEditado_quandoEditarSiteComMso() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoNivelMso());

        when(usuarioRepository.findCoordenadoresDisponiveisExcetoPorSiteId(new SitePredicate().build(), 1))
            .thenReturn(List.of(new UsuarioNomeResponse(11125, "Coordenador2 operacao ativo local", ESituacao.A)));

        assertThat(usuarioSiteService.buscarCoordenadoresDisponiveisEVinculadosAoSite(1))
            .extracting(UsuarioNomeResponse::getId, UsuarioNomeResponse::getNome)
            .contains(tuple(11125, "Coordenador2 operacao ativo local"));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void getSupervisoresSemSitePorCoordenadorsId_deveRetornarSupevisoresDisponiveisEVinculadosAoCoordenador_quandoReceberCoordenadoresIds() {
        var coordenadoresIds = List.of(11122);
        var sitePredicate = new SitePredicate()
            .comSupervisoresDisponiveisDosCoordenadores(coordenadoresIds);

        when(usuarioRepository.findSupervisoresSemSitePorCoordenadorId(sitePredicate.build()))
            .thenReturn(List.of(new UsuarioNomeResponse(11124, "Supervisor2 operacao ativo local", ESituacao.A)));

        assertThat(usuarioSiteService.getSupervisoresSemSitePorCoordenadorsId(coordenadoresIds))
            .extracting(UsuarioNomeResponse::getId)
            .contains(11124);
    }

    @Test
    @SuppressWarnings("LineLength")
    public void buscarSupervisoresDisponiveisEVinculadosAoSite_deveRetornarSupervisoresDisponiveisEVinculadoAoSite_quandoReceberCoordenadorESiteId() {
        when(siteRepository.findBySupervisorId(11123)).thenReturn(new Site());

        var coordenadoresIds = List.of(11122);
        var sitePredicate = new SitePredicate()
            .comSupervisoresDisponiveisDosCoordenadoresEsite(coordenadoresIds, 1);

        when(usuarioRepository.findSupervisoresSemSitePorCoordenadorId(sitePredicate.build()))
            .thenReturn(umaListaDeSupervisores());

        assertNotNull(siteRepository.findBySupervisorId(11123));
        assertThat(usuarioSiteService.buscarSupervisoresDisponiveisEVinculadosAoSite(coordenadoresIds, 1))
            .hasSize(2);
    }

    @Test
    public void getSupervisoresSemSitePorCoordenadorsId_deveretornarVazio_quandoEditarSiteSemSupervisoresDisponiveis() {
        assertThat(usuarioSiteService.getSupervisoresSemSitePorCoordenadorsId(List.of(11126)))
            .hasSize(0);
    }

    @Test
    @Ignore
    public void buscarCoordenadoresDisponiveis_deveRetornarListaVazia_quandoCoordenadoresNaoDisponiveisParaCidade() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoNivelMso());
        assertThat(usuarioSiteService.buscarCoordenadoresDisponiveis())
            .hasSize(0);
    }

    private UsuarioAutenticado umUsuarioAutenticadoPadrao() {
        return UsuarioAutenticado.builder()
            .id(10)
            .nivelCodigo(CodigoNivel.OPERACAO.name())
            .usuario(buildUsuario())
            .build();
    }

    private List<UsuarioNomeResponse> umaListaDeCoordenadores() {
        return List.of(
            new UsuarioNomeResponse(11125, "Coordenador2 operacao ativo local", ESituacao.A),
            new UsuarioNomeResponse(11126, "Coordenador sem site operacao ativo local", ESituacao.A));
    }

    private List<UsuarioNomeResponse> umaListaDeSupervisores() {
        return List.of(
            new UsuarioNomeResponse(11123, "Supervisor operacao ativo local", ESituacao.A),
            new UsuarioNomeResponse(11124, "Supervisor2 sem site operacao ativo local", ESituacao.A));
    }

    public static UsuarioNomeResponse umUsuarioNomeResponse(Integer id) {
        return UsuarioNomeResponse.builder()
            .id(id)
            .build();
    }
}
