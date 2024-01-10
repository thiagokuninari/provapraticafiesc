package br.com.xbrain.autenticacao.modules.site.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.site.dto.SiteDiscadoraRequest;
import br.com.xbrain.autenticacao.modules.site.dto.SiteFiltros;
import br.com.xbrain.autenticacao.modules.site.dto.SiteRequest;
import br.com.xbrain.autenticacao.modules.site.helper.SiteHelper;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.site.helper.SiteHelper.*;
import static helpers.TestsHelper.convertObjectToJsonBytes;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import(OAuth2ResourceConfig.class)
@WebMvcTest(controllers = SiteController.class)
public class SiteControllerTest {

    private static final String API_URI = "/api/sites";
    private static final String VISUALIZAR_SITES = "AUT_2046";
    private static final String GERENCIAR_SITES = "AUT_2047";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private SiteService service;
    @MockBean
    private TokenStore tokenStore;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @MockBean
    private UsuarioSubCanalObserver usuarioSubCanalObserver;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getSites_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getAll(any(), any());
    }

    @Test
    @SneakyThrows
    public void getSites_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URI))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).getAll(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void getSites_deveRetornarOk_quandoDadosValidos() {
        when(service.getAll(new SiteFiltros(), new PageRequest())).thenReturn(new PageImpl<>(List.of()));

        mvc.perform(get(API_URI)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).getAll(new SiteFiltros(), new PageRequest());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void exportarCsv_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/exportar-csv"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    public void exportarCsv_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URI + "/exportar-csv"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void exportarCsv_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/exportar-csv"))
            .andExpect(status().isOk());

        verify(service).gerarRelatorioDiscadorasCsv(any(), any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getSitesByEstadoId_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/estado/2"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getSitesByEstadoId(any());
    }

    @Test
    @SneakyThrows
    public void getSitesByEstadoId_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URI + "/estado/{estadoId}", 2))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).getSitesByEstadoId(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void getSitesByEstadoId_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/estado/{estadoId}", 2))
            .andExpect(status().isOk());

        verify(service).getSitesByEstadoId(2);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllAtivos_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/ativos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    public void getAllAtivos_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URI + "/ativos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void getAllAtivos_deveRetornarOK_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/ativos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).getAllAtivos(new SiteFiltros());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllSupervisoresBySiteId_deveRetornarOk_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/{id}/supervisores", 101))
            .andExpect(status().isOk());

        verify(service).getAllSupervisoresBySiteId(101);
    }

    @Test
    @SneakyThrows
    public void getAllSupervisoresBySiteId_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/{id}/supervisores", 101))
            .andExpect(status().isOk());

        verify(service).getAllSupervisoresBySiteId(101);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllSupervisoresByHierarquia_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/{id}/supervisores/hierarquia/{usuarioSuperiorId}", 100, 300))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getAllSupervisoresByHierarquia(any(), any());
    }

    @Test
    @SneakyThrows
    public void getAllSupervisoresByHierarquia_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URI + "/{id}/supervisores/hierarquia/{usuarioSuperiorId}", 100, 300))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).getAllSupervisoresByHierarquia(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void getAllSupervisoresByHierarquia_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/{id}/supervisores/hierarquia/{usuarioSuperiorId}", 100, 300))
            .andExpect(status().isOk());

        verify(service).getAllSupervisoresByHierarquia(100, 300);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getById_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    public void getById_deveRetornarOK_quandoDadosOk() {
        when(service.findById(1)).thenReturn(SiteHelper.umSiteCompleto());

        mvc.perform(get(API_URI + "/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).findById(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getDetalheSiteById_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/{id}/detalhe", 100))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).findById(any());
    }

    @Test
    @SneakyThrows
    public void getDetalheSiteById_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URI + "/{id}/detalhe", 100))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).findById(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void getDetalheSiteById_deveRetornarOk_quandoNaoTiverPermissao() {
        when(service.findById(100)).thenReturn(umSiteCompleto());

        mvc.perform(get(API_URI + "/{id}/detalhe", 100))
            .andExpect(status().isOk());

        verify(service).getSiteDetalheResponseById(100);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosIdsBySiteId_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/{id}/usuarios/ids", 100))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getUsuariosIdsBySiteId(any());
    }

    @Test
    @SneakyThrows
    public void getUsuariosIdsBySiteId_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URI + "/{id}/usuarios/ids", 100))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).getUsuariosIdsBySiteId(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void getUsuariosIdsBySiteId_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/{id}/usuarios/ids", 100))
            .andExpect(status().isOk());

        verify(service).getUsuariosIdsBySiteId(100);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void save_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(post(API_URI))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).save(any());
    }

    @Test
    @SneakyThrows
    public void save_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(post(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umSiteRequest())))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).save(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_SITES})
    public void save_deveRetornarBadRequest_quandoDadoObrigatorioNull() {
        mvc.perform(post(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new SiteRequest())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo supervisoresIds é obrigatório.",
                "O campo coordenadoresIds é obrigatório.",
                "O campo nome é obrigatório.",
                "O campo estadosIds é obrigatório.",
                "O campo timeZone é obrigatório.")));

        verify(service, never()).save(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_SITES})
    public void save_deveRetornarBadRequest_quandoDadoObrigatorioEmpty() {
        mvc.perform(post(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umSiteRequestEmpty())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo supervisoresIds é obrigatório.",
                "O campo coordenadoresIds é obrigatório.",
                "O campo estadosIds é obrigatório.")));

        verify(service, never()).save(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_SITES})
    public void save_deveRetornarOk_quandoDadoObrigatorioBlank() {
        var requestBlank = umSiteRequest();
        requestBlank.setNome("   ");

        when(service.save(requestBlank)).thenReturn(umSiteCompleto());

        mvc.perform(post(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(requestBlank)))
            .andExpect(status().isOk());

        verify(service).save(eq(requestBlank));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_SITES})
    public void save_deveRetornarOk_quandoDadosValidos() {
        when(service.save(umSiteRequest())).thenReturn(umSiteCompleto());

        mvc.perform(post(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umSiteRequest())))
            .andExpect(status().isOk());

        verify(service).save(eq(umSiteRequest()));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void update_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(put(API_URI))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).update(any());
    }

    @Test
    @SneakyThrows
    public void update_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).update(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_SITES})
    public void update_deveRetornarBadRequest_quandoDadoObrigatorioNull() {
        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new SiteRequest())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo supervisoresIds é obrigatório.",
                "O campo coordenadoresIds é obrigatório.",
                "O campo nome é obrigatório.",
                "O campo estadosIds é obrigatório.",
                "O campo timeZone é obrigatório.")));

        verify(service, never()).update(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_SITES})
    public void update_deveRetornarBadRequest_quandoDadoObrigatorioListEmpty() {
        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umSiteRequestEmpty())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo supervisoresIds é obrigatório.",
                "O campo coordenadoresIds é obrigatório.",
                "O campo estadosIds é obrigatório.")));

        verify(service, never()).update(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_SITES})
    public void update_deveRetornarOk_quandoDadoObrigatorioStringBlank() {
        var requestBlank = umSiteRequest();
        requestBlank.setNome("   ");

        when(service.update(requestBlank)).thenReturn(umSiteCompleto());

        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(requestBlank)))
            .andExpect(status().isOk());

        verify(service).update(requestBlank);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_SITES})
    public void update_deveRetornarOk_quandoDadosValidos() {
        when(service.update(umSiteRequest())).thenReturn(umSiteCompleto());

        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umSiteRequest())))
            .andExpect(status().isOk());

        verify(service).update(umSiteRequest());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void inativar_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(put(API_URI + "/{id}/inativar", 1))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).inativar(any());
    }

    @Test
    @SneakyThrows
    public void inativar_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(put(API_URI + "/{id}/inativar", 1))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).inativar(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_SITES})
    public void inativar_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(put(API_URI + "/{id}/inativar", 1))
            .andExpect(status().isOk());

        verify(service).inativar(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarEstadosDisponiveis_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/estados-disponiveis")
                .param("siteIgnoradoId", "1"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    public void buscarEstadosDisponiveis_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URI + "/estados-disponiveis"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void buscarEstadosDisponiveis_deveRetornarOk_quandoSiteIgnoradoIdNaoInformado() {
        mvc.perform(get(API_URI + "/estados-disponiveis"))
            .andExpect(status().isOk());

        verify(service).buscarEstadosNaoAtribuidosEmSites(null);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void buscarEstadosDisponiveis_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/estados-disponiveis")
                .param("siteIgnoradoId", "1"))
            .andExpect(status().isOk());

        verify(service).buscarEstadosNaoAtribuidosEmSites(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarSitesVinculadosAoUsuarioLogado_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/usuario-logado"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void buscarSitesVinculadosAoUsuarioLogado_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/usuario-logado"))
            .andExpect(status().isOk());

        verify(service).getAllByUsuarioLogado();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarCidadesDisponiveisPorEstadosIds_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/cidades-disponiveis")
                .param("estadosIds", "1")
                .param("siteIgnoradoId", "2"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void buscarCidadesDisponiveisPorEstadosIds_deveRetornarBadRequest_quandoEstadosIdsNaoInformado() {
        mvc.perform(get(API_URI + "/cidades-disponiveis")
                .param("siteIgnoradoId", "2"))
            .andExpect(status().isBadRequest());

        verify(service, never()).buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void buscarCidadesDisponiveisPorEstadosIds_deveRetornarOk_quandoSiteIgnoradoIdsNaoInformado() {
        mvc.perform(get(API_URI + "/cidades-disponiveis")
                .param("estadosIds", "1"))
            .andExpect(status().isOk());

        verify(service).buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(List.of(1), null);
    }

    @Test
    @SneakyThrows
    public void buscarCidadesDisponiveisPorEstadosIds_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URI + "/cidades-disponiveis")
                .param("estadosIds", "1")
                .param("siteIgnoradoId", "2"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void buscarCidadesDisponiveisPorEstadosIds_deveRetornarOk_quandoDadosValiddos() {
        mvc.perform(get(API_URI + "/cidades-disponiveis")
                .param("estadosIds", "1")
                .param("siteIgnoradoId", "2"))
            .andExpect(status().isOk());

        verify(service).buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(List.of(1), 2);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void adicionarDiscadora_deveRetornarUnauthorized_quandoNaoPassarToken() {
        var umSiteDiscadoraRequest = new SiteDiscadoraRequest();

        mvc.perform(put(API_URI + "/adicionar-discadora")
                .contentType(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umSiteDiscadoraRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    public void adicionarDiscadora_deveRetornarForbidden_quandoNaoTiverPermissa() {
        var umSiteDiscadoraRequest = new SiteDiscadoraRequest();

        mvc.perform(put(API_URI + "/adicionar-discadora")
                .contentType(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umSiteDiscadoraRequest)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_SITES})
    public void adicionarDiscadora_deveRetornarOk_quandoDadosValidos() {
        var umSiteDiscadoraRequest = new SiteDiscadoraRequest();

        mvc.perform(put(API_URI + "/adicionar-discadora")
                .contentType(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umSiteDiscadoraRequest)))
            .andExpect(status().isOk());

        verify(service).adicionarDiscadora(umSiteDiscadoraRequest.getDiscadoraId(), umSiteDiscadoraRequest.getSites());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void removerDiscadora_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(put(API_URI + "/remover-discadora")
                .contentType(javax.ws.rs.core.MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    public void removerDiscadora_deveRetornarForbidden_quandoNaoTiverPermissao() {
        var umSiteDiscadoraRequest = new SiteDiscadoraRequest();

        mvc.perform(put(API_URI + "/remover-discadora")
                .contentType(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umSiteDiscadoraRequest)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_SITES})
    public void removerDiscadora_deveRetornarOk_quandoDadosValidos() {
        var umSiteDiscadoraRequest = new SiteDiscadoraRequest();

        mvc.perform(put(API_URI + "/remover-discadora")
                .contentType(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umSiteDiscadoraRequest)))
            .andExpect(status().isOk());

        verify(service).removerDiscadora(umSiteDiscadoraRequest.getSiteId());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getSiteBySupervisorId_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/supervisor/{supervisorId}", 1))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getSiteBySupervisorId(any());
    }

    @Test
    @SneakyThrows
    public void getSiteBySupervisorId_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URI + "/supervisor/{supervisorId}", 102))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).getSiteBySupervisorId(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void getSiteBySupervisorId_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/supervisor/{supervisorId}", 102))
            .andExpect(status().isOk());

        verify(service).getSiteBySupervisorId(eq(102));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findSitesPermitidosAoUsuarioAutenticado_deveRetornarOk_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/permitidos", 1))
            .andExpect(status().isOk());

        verify(service).findSitesPermitidosAoUsuarioAutenticado();
    }

    @Test
    @SneakyThrows
    public void findSitesPermitidosAoUsuarioAutenticado_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/permitidos", 1))
            .andExpect(status().isOk());

        verify(service).findSitesPermitidosAoUsuarioAutenticado();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarAssistentesAtivosDaHierarquiaDosUsuariosSuperioresIds_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/assistentes-da-hierarquia/1"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).buscarAssistentesAtivosDaHierarquiaDosUsuariosSuperioresIds(any());
    }

    @Test
    @SneakyThrows
    public void buscarAssistentesAtivosDaHierarquiaDosUsuariosSuperioresIds_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URI + "/assistentes-da-hierarquia/1"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).buscarAssistentesAtivosDaHierarquiaDosUsuariosSuperioresIds(any());

    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void buscarAssistentesAtivosDaHierarquiaDosUsuariosSuperioresIds_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/assistentes-da-hierarquia/1"))
            .andExpect(status().isOk());

        verify(service).buscarAssistentesAtivosDaHierarquiaDosUsuariosSuperioresIds(List.of(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarVendedoresAtivosDaHierarquiaDoUsuarioSuperiorIdSemEquipeVenda_deveRetornarUnauthorized_quandoSemToken() {
        mvc.perform(get(API_URI + "/vendedores-da-hierarquia/{usuarioSuperiorId}/sem-equipe-venda", 1))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).buscarVendedoresAtivosDaHierarquiaDoUsuarioSuperiorIdSemEquipeVenda(any());
    }

    @Test
    @SneakyThrows
    public void buscarVendedoresAtivosDaHierarquiaDoUsuarioSuperiorIdSemEquipeVenda_deveRetornarForbidden_quandoSemPermissao() {
        mvc.perform(get(API_URI + "/vendedores-da-hierarquia/{usuarioSuperiorId}/sem-equipe-venda", 1))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).buscarVendedoresAtivosDaHierarquiaDoUsuarioSuperiorIdSemEquipeVenda(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void buscarVendedoresAtivosDaHierarquiaDoUsuarioSuperiorIdSemEquipeVenda_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/vendedores-da-hierarquia/{usuarioSuperiorId}/sem-equipe-venda", 1))
            .andExpect(status().isOk());

        verify(service).buscarVendedoresAtivosDaHierarquiaDoUsuarioSuperiorIdSemEquipeVenda(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarCoordenadoresIdsAtivosDoUsuarioId_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/coordenadores/1"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).buscarCoordenadoresIdsAtivosDoUsuarioId(any());
    }

    @Test
    @SneakyThrows
    public void buscarCoordenadoresIdsAtivosDoUsuarioId_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URI + "/coordenadores/1"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).buscarCoordenadoresIdsAtivosDoUsuarioId(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void buscarCoordenadoresIdsAtivosDoUsuarioId_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/coordenadores/1"))
            .andExpect(status().isOk());

        verify(service).buscarCoordenadoresIdsAtivosDoUsuarioId(eq(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarSiteCidadePorCidadeUf_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/cidade-uf/LONDRINA/PR"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).buscarSiteCidadePorCidadeUf(any(), any());
    }

    @Test
    @SneakyThrows
    public void buscarSiteCidadePorCidadeUf_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URI + "/cidade-uf/LONDRINA/PR"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).buscarSiteCidadePorCidadeUf(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void buscarSiteCidadePorCidadeUf_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/cidade-uf/LONDRINA/PR"))
            .andExpect(status().isOk());

        verify(service).buscarSiteCidadePorCidadeUf(eq("LONDRINA"), eq("PR"));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarSiteCidadePorCodigoCidadeDbm_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/codigo-cidade-dbm/1"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).buscarSiteCidadePorCodigoCidadeDbm(any());
    }

    @Test
    @SneakyThrows
    public void buscarSiteCidadePorCodigoCidadeDbm_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URI + "/codigo-cidade-dbm/1"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).buscarSiteCidadePorCodigoCidadeDbm(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void buscarSiteCidadePorCodigoCidadeDbm_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/codigo-cidade-dbm/1"))
            .andExpect(status().isOk());

        verify(service).buscarSiteCidadePorCodigoCidadeDbm(eq(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarSiteCidadePorDdd_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/ddd/43"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).buscarSiteCidadePorDdd(any());
    }

    @Test
    @SneakyThrows
    public void buscarSiteCidadePorDdd_deveRetornarforbidden_quandoUsuarioNaoPossuiPermissao() {
        mvc.perform(get(API_URI + "/ddd/43"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).buscarSiteCidadePorDdd(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void buscarSiteCidadePorDdd_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/ddd/43"))
            .andExpect(status().isOk());

        verify(service).buscarSiteCidadePorDdd(eq(43));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarTodos_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI + "/buscar/todos"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).buscarTodos(any());
    }

    @Test
    @SneakyThrows
    public void buscarTodos_deveRetornarforbidden_quandoUsuarioNaoPossuiPermissao() {
        mvc.perform(get(API_URI + "/buscar/todos"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).buscarTodos(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_SITES})
    public void buscarTodos_deveRetornarOK_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/buscar/todos"))
            .andExpect(status().isOk());

        verify(service).buscarTodos(any());
    }
}
