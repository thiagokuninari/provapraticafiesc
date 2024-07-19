package br.com.xbrain.autenticacao.modules.horarioacesso.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.service.HorarioAcessoService;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.horarioacesso.helper.HorarioHelper.*;
import static br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.OrganizacaoEmpresaHelper.convertObjectToJsonBytes;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import(OAuth2ResourceConfig.class)
@WebMvcTest(controllers = HorarioAcessoController.class)
public class HorarioAcessoControllerTest {

    private static final String URL = "/api/horarios-acesso";
    private static final String GERENCIAR_HORARIOS_ACESSO = "AUT_20009";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private TokenStore tokenStore;
    @MockBean
    private HorarioAcessoService service;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @MockBean
    private UsuarioSubCanalObserver usuarioSubCanalObserver;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getHorariosAcesso_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getHorariosAcesso(any(), any());
    }

    @Test
    @SneakyThrows
    public void getHorariosAcesso_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).getHorariosAcesso(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_HORARIOS_ACESSO})
    public void getHorariosAcesso_deveRetornarOk_quandoTiverPermissao() {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).getHorariosAcesso(new PageRequest(), new HorarioAcessoFiltros());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getHorarioAcesso_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(URL + "/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getHorarioAcesso(any());
    }

    @Test
    @SneakyThrows
    public void getHorarioAcesso_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).getHorarioAcesso(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_HORARIOS_ACESSO})
    public void getHorarioAcesso_deveRetornarOk_quandoTiverPermissao() {
        when(service.getHorarioAcesso(1)).thenReturn(umHorarioAcessoResponse());

        mvc.perform(get(URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umHorarioAcessoResponse())))
            .andExpect(status().isOk());

        verify(service).getHorarioAcesso(eq(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getHistoricos_deveRetornarUnauthorized_quandoTiverPermissao() {
        when(service.getHistoricos(any(PageRequest.class), anyInt())).thenReturn(umaListaHorarioHistoricoResponse());

        mvc.perform(get(URL + "/1/historico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umaListaHorarioHistoricoResponse())))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getHistoricos(any(), any());
    }

    @Test
    @SneakyThrows
    public void getHistoricos_deveRetornarForbidden_quandoNaoTiverPermissao() {
        when(service.getHistoricos(any(PageRequest.class), anyInt())).thenReturn(umaListaHorarioHistoricoResponse());

        mvc.perform(get(URL + "/1/historico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umaListaHorarioHistoricoResponse())))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).getHistoricos(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_HORARIOS_ACESSO})
    public void getHistoricos_deveRetornarOk_quandoTiverPermissao() {
        when(service.getHistoricos(new PageRequest(), 1)).thenReturn(umaListaHorarioHistoricoResponse());

        mvc.perform(get(URL + "/1/historico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umaListaHorarioHistoricoResponse())))
            .andExpect(status().isOk());

        verify(service).getHistoricos(new PageRequest(), 1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void save_deveRetornarUnauthorized_quandoTokenInvalido() {
        mvc.perform(post(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).save(any());
    }

    @Test
    @SneakyThrows
    public void save_deveRetornarForbidden_quandoUsuarioNaoTiverPermissao() {
        mvc.perform(post(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).save(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_HORARIOS_ACESSO})
    public void save_deveRetornarBadRequest_quandoDadosObrigatoriosNull() {
        mvc.perform(post(URL)
                .content(convertObjectToJsonBytes(new HorarioAcessoRequest()))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo siteId é obrigatório.",
                "O campo horariosAtuacao é obrigatório.")));

        verify(service, never()).save(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_HORARIOS_ACESSO})
    public void save_deveRetornarOK_quandoDadosObrigatoriosListEmpty() {
        var requestEmpty = umHorarioAcessoRequest();
        requestEmpty.setHorariosAtuacao(List.of());

        when(service.save(requestEmpty)).thenReturn(umHorarioAcesso());

        mvc.perform(post(URL)
                .content(convertObjectToJsonBytes(requestEmpty))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).save(requestEmpty);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_HORARIOS_ACESSO})
    public void save_deveRetornarOk_quandoDadosValidos() {
        when(service.save(umHorarioAcessoRequest())).thenReturn(umHorarioAcesso());

        mvc.perform(post(URL)
                .content(convertObjectToJsonBytes(umHorarioAcessoRequest()))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).save(eq(umHorarioAcessoRequest()));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getHorarioAcessoStatus_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(URL + "/status")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    public void getHorarioAcessoStatus_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(URL + "/status")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_HORARIOS_ACESSO})
    public void getHorarioAcessoStatus_deveRetornarOK_quandoDadosValidos() {
        mvc.perform(get(URL + "/status")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Usuario-Canal", ECanal.AGENTE_AUTORIZADO))
            .andExpect(status().isOk());

        verify(service).getStatus(ECanal.AGENTE_AUTORIZADO);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getHorarioAcessoStatusByIdSite_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(URL + "/status/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    public void getHorarioAcessoStatusByIdSite_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(URL + "/status/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_HORARIOS_ACESSO})
    public void getHorarioAcessoStatusByIdSite_deveRetornarOK_quandoDadosValidos() {
        mvc.perform(get(URL + "/status/1")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Usuario-Canal", ECanal.AGENTE_AUTORIZADO))
            .andExpect(status().isOk());

        verify(service).getStatus(ECanal.AGENTE_AUTORIZADO, 1);
    }
}
