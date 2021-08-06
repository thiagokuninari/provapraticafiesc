package br.com.xbrain.autenticacao.modules.site.service;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.site.dto.SiteRequest;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.repository.SiteRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioSiteService;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.buildUsuario;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelMso;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Sql(scripts = {"classpath:/tests_sites_IT.sql"})
@Transactional
public class SiteServiceIT {

    @MockBean
    private AutenticacaoService autenticacaoService;
    @SpyBean
    private UsuarioSiteService usuarioSiteService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private SiteService siteService;
    @Autowired
    private SiteRepository siteRepository;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;

    @Test
    public void buscarCoordenadores_deveRetornarCoordendoresDaHierarquiaDoUsuarioLogado_quandoSolicitarPorCidade() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoPadrao());
        doReturn(List.of(11125)).when(usuarioSiteService).getSubordinadosPorIdECargo(any(),any());
        var coordenadoresDisponiveis = usuarioSiteService.buscarCoordenadoresDisponiveis();
        assertThat(coordenadoresDisponiveis)
            .extracting(UsuarioNomeResponse::getId, UsuarioNomeResponse::getNome)
            .containsExactly(Tuple.tuple(11125, "Coordenador2 operacao ativo local"));
    }

    @Test
    public void buscarCoordenadoresDisponiveis_deveRetornarCoordenadoresDisponiveisEIgnoraSiteInativo_quandoSolicitarPorCidade() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoPadrao());
        doReturn(List.of(11125, 11126)).when(usuarioSiteService).getSubordinadosPorIdECargo(any(),any());
        assertThat(siteRepository.findById(2).get())
            .extracting(Site::getCoordenadores, Site::getId)
            .contains(Set.of(new Usuario(11126)), 2);

        var coordenadoresDisponiveis = usuarioSiteService.buscarCoordenadoresDisponiveis();
        assertThat(coordenadoresDisponiveis)
            .extracting(UsuarioNomeResponse::getId, UsuarioNomeResponse::getNome)
            .containsExactly(
                tuple(11125, "Coordenador2 operacao ativo local"),
                tuple(11126, "Coordenador sem site operacao ativo local"));
    }

    @Test
    public void buscarComMso_deveRetornarTodosOsCoordenadoresDisponiveisPorCidade_quandoUsuarioLogadoForMso() {

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoNivelMso());
        assertThat(usuarioSiteService.buscarCoordenadoresDisponiveis())
            .extracting(UsuarioNomeResponse::getId, UsuarioNomeResponse::getNome)
            .containsExactly(
                tuple(11125, "Coordenador2 operacao ativo local"),
                tuple(11126, "Coordenador sem site operacao ativo local"));
    }

    @Test
    public void buscarEditar_deveRetornarCoordenadoresDisponiveisENoSiteSendoEditado_quandoEditarSiteComMso() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoNivelMso());

        assertThat(usuarioSiteService.buscarCoordenadoresDisponiveisEVinculadosAoSite(1))
            .extracting(UsuarioNomeResponse::getId, UsuarioNomeResponse::getNome)
            .contains(tuple(11125, "Coordenador2 operacao ativo local"));
    }

    @Test
    public void supervisor_deveRetornarSupevisoresDisponiveisEVinculadosAoCoordenador_quandoReceberCoordenadoresIds() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoNivelMso());

        assertThat(usuarioSiteService.getSupervisoresSemSitePorCoordenadorsId(List.of(11122)))
            .extracting(UsuarioNomeResponse::getId)
            .contains(11124);
    }

    @Test
    public void supervisorEditar_deveRetornarSupervisoresDisponiveisEVinculadoAoSite_quandoReceberCoordenadorESiteId() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoNivelMso());

        assertNotNull(siteRepository.findBySupervisorId(11123));
        assertThat(usuarioSiteService.buscarSupervisoresDisponiveisEVinculadosAoSite(List.of(11122), 1))
            .hasSize(2);
    }

    @Test
    public void supervisoresNaoDisponiveis_deveretornarVazio_quandoEditarSiteSemSupervisoresDisponiveis() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoNivelMso());
        assertThat(usuarioSiteService.getSupervisoresSemSitePorCoordenadorsId(List.of(11126)))
            .hasSize(0);
    }

    @Test
    public void coordenadoresNaoDisponiveis_deveRetornarListaVazia_quandoCoordenadoresNaoDisponiveisParaCidade() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoNivelMso());
        assertThat(usuarioSiteService.buscarCoordenadoresDisponiveis())
            .hasSize(0);
    }

    @Test
    public void editarException_deveLancarException_quandoSupervisorRemovidoEstiverEmEquipeVendas() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoNivelMso());
        when(equipeVendaD2dService.getEquipeVendas(any())).thenReturn(
            List.of(EquipeVendaDto.builder().descricao("Equipe 1").build()));
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> siteService.update(requestUpdateSite()))
            .withMessage("Para concluir essa operação é necessário remover o supervisor(a) "
                + "Supervisor2 operacao ativo local da equipe de vendas Equipe 1.");
    }

    @Test
    public void editarSucesso_deveEditarSiteComSucesso_quandoSupervisorRemovidoNaoEstiverVinculadoAEquipe() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoNivelMso());
        when(equipeVendaD2dService.getEquipeVendas(any())).thenReturn(List.of());
        assertThatCode(() -> siteService.update(requestUpdateSite()))
            .doesNotThrowAnyException();
    }

    private SiteRequest requestUpdateSite() {
        return SiteRequest.builder()
            .id(1)
            .supervisoresIds(List.of(11123))
            .nome("Manaus")
            .coordenadoresIds(List.of(11122))
            .cidadesIds(List.of(1500))
            .estadosIds(List.of(200))
            .build();
    }

    private UsuarioAutenticado umUsuarioAutenticadoPadrao() {
        return UsuarioAutenticado.builder()
            .id(10)
            .nivelCodigo(CodigoNivel.OPERACAO.name())
            .usuario(buildUsuario())
            .build();
    }
}
