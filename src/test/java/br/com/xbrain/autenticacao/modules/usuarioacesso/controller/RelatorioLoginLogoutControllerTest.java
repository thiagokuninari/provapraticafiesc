package br.com.xbrain.autenticacao.modules.usuarioacesso.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.RelatorioLoginLogoutRequest;
import br.com.xbrain.autenticacao.modules.usuarioacesso.service.RelatorioLoginLogoutService;
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

import java.time.LocalDate;

import static br.com.xbrain.autenticacao.modules.usuarioacesso.helper.LoginLogoutHelper.umRelatorio;
import static br.com.xbrain.autenticacao.modules.usuarioacesso.helper.LoginLogoutHelper.umaListaLoginLogoutResponse;
import static helpers.TestsHelper.convertObjectToJsonBytes;
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
@WebMvcTest(controllers = RelatorioLoginLogoutController.class)
public class RelatorioLoginLogoutControllerTest {

    private static final String API_URL = "/api/relatorio-login-logout";
    private static final String VISUALIZAR_RELATORIO_LOGIN_LOGOUT = "AUT_2100";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private TokenStore tokenStore;
    @MockBean
    private RelatorioLoginLogoutService service;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @MockBean
    private UsuarioSubCanalObserver usuarioSubCanalObserver;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getLoginsLogoutsDeHoje_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URL + "/hoje")
                .header("X-Usuario-Canal", ECanal.AGENTE_AUTORIZADO)
                .param("agenteAutorizadoId", "1"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getLoginsLogoutsDeHoje(any(), any(), any(),any());
    }

    @Test
    @SneakyThrows
    public void getLoginsLogoutsDeHoje_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URL + "/hoje")
                .header("X-Usuario-Canal", ECanal.AGENTE_AUTORIZADO)
                .param("agenteAutorizadoId", "1"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).getLoginsLogoutsDeHoje(any(), any(), any(),any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_RELATORIO_LOGIN_LOGOUT})
    public void getLoginsLogoutsDeHoje_deveRetornarBadRequest_quandoCanalNaoInformado() {
        mvc.perform(get(API_URL + "/hoje")
                .param("agenteAutorizadoId", "1"))
            .andExpect(status().isBadRequest());

        verify(service, never()).getLoginsLogoutsDeHoje(any(), any(), any(),any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_RELATORIO_LOGIN_LOGOUT})
    public void getLoginsLogoutsDeHoje_deveRetornarOK_quandoAgenteAutorizadoIdNaoInformado() {
        mvc.perform(get(API_URL + "/hoje")
                .header("X-Usuario-Canal", ECanal.AGENTE_AUTORIZADO))
            .andExpect(status().isOk());

        verify(service).getLoginsLogoutsDeHoje(new PageRequest(), ECanal.AGENTE_AUTORIZADO, null,null);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_RELATORIO_LOGIN_LOGOUT})
    public void getLoginsLogoutsDeHoje_deveRetornarOK_quandoDadosValidos() {
        mvc.perform(get(API_URL + "/hoje")
                .header("X-Usuario-Canal", ECanal.AGENTE_AUTORIZADO)
                .param("agenteAutorizadoId", "1")
                .param("subCanalId", "2"))
            .andExpect(status().isOk());

        verify(service).getLoginsLogoutsDeHoje(new PageRequest(), ECanal.AGENTE_AUTORIZADO,1,2);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getLoginsLogoutsEntreDatas_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URL + "/entre-datas"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).buscarAcessosEntreDatasPorUsuarios(any());
    }

    @Test
    @SneakyThrows
    public void getLoginsLogoutsEntreDatas_deveRetornarBadRequest_quandoDadosObrigatoriosNull() {
        mvc.perform(post(API_URL + "/entre-datas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new RelatorioLoginLogoutRequest())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo usuariosIds é obrigatório.",
                "O campo dataFinal é obrigatório.",
                "O campo dataInicial é obrigatório.")));

        verify(service, never()).buscarAcessosEntreDatasPorUsuarios(any());
    }

    @Test
    @SneakyThrows
    public void getLoginsLogoutsEntreDatas_deveRetornarBadRequest_quandoDataInicialPosteriorDataFinal() {
        var relatorio = umRelatorio();
        relatorio.setDataInicial(LocalDate.of(2023, 6, 25));
        when(service.buscarAcessosEntreDatasPorUsuarios(relatorio)).thenReturn(umaListaLoginLogoutResponse());

        mvc.perform(post(API_URL + "/entre-datas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(relatorio)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message",
                containsInAnyOrder("Data inicial não pode ser posterior a data final.")));

        verify(service, never()).buscarAcessosEntreDatasPorUsuarios(any());
    }

    @Test
    @SneakyThrows
    public void getLoginsLogoutsEntreDatas_deveRetornarBadRequest_quandoDataFinalPosteriorAAtual() {
        var relatorio = umRelatorio();
        relatorio.setDataFinal(LocalDate.now().plusDays(2));
        when(service.buscarAcessosEntreDatasPorUsuarios(relatorio)).thenReturn(umaListaLoginLogoutResponse());

        mvc.perform(post(API_URL + "/entre-datas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(relatorio)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message",
                containsInAnyOrder("Data final não pode ser posterior a atual.")));

        verify(service, never()).buscarAcessosEntreDatasPorUsuarios(any());
    }

    @Test
    @SneakyThrows
    public void getLoginsLogoutsEntreDatas_deveRetornarOK_quandoDadosValidos() {
        when(service.buscarAcessosEntreDatasPorUsuarios(umRelatorio())).thenReturn(umaListaLoginLogoutResponse());

        mvc.perform(post(API_URL + "/entre-datas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umRelatorio())))
            .andExpect(status().isOk());

        verify(service).buscarAcessosEntreDatasPorUsuarios(umRelatorio());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getCsv_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URL + "/csv")
                .header("X-Usuario-Canal", ECanal.AGENTE_AUTORIZADO))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getCsv(any(), any(), any(), any(),any());
    }

    @Test
    @SneakyThrows
    public void getCsv_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URL + "/csv")
                .header("X-Usuario-Canal", ECanal.AGENTE_AUTORIZADO))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).getCsv(any(), any(), any(), any(),any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_RELATORIO_LOGIN_LOGOUT})
    public void getCsv_deveRetornarBadRequest_quandoFiltroComCampoObrigatorioNull() {
        mvc.perform(get(API_URL + "/csv")
                .header("X-Usuario-Canal", ECanal.AGENTE_AUTORIZADO)
                .param("agenteAutorizadoId", "1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo dataInicio é obrigatório.",
                "O campo dataFim é obrigatório.",
                "O campo colaboradoresIds é obrigatório.")));

        verify(service, never()).getCsv(any(), any(), any(), any(),any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_RELATORIO_LOGIN_LOGOUT})
    public void getCsv_deveRetornarBadRequest_quandoCanalNaoInformado() {
        mvc.perform(get(API_URL + "/csv")
                .param("colaboradoresIds", "2")
                .param("dataInicio", "20/06/2023")
                .param("dataFim", "29/06/2023")
                .param("agenteAutorizadoId", "1"))
            .andExpect(status().isBadRequest());

        verify(service, never()).getCsv(any(), any(), any(), any(),any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_RELATORIO_LOGIN_LOGOUT})
    public void getCsv_deveRetornarOK_quandoAgenteAutorizadoIdNaoInformado() {
        mvc.perform(get(API_URL + "/csv")
                .header("X-Usuario-Canal", ECanal.AGENTE_AUTORIZADO)
                .param("colaboradoresIds", "2")
                .param("dataInicio", "20/06/2023")
                .param("dataFim", "29/06/2023"))
            .andExpect(status().isOk());

        verify(service).getCsv(any(), any(), eq(ECanal.AGENTE_AUTORIZADO), eq(null),eq(null));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_RELATORIO_LOGIN_LOGOUT})
    public void getCsv_deveRetornarOK_quandoDadosValidos() {
        mvc.perform(get(API_URL + "/csv")
                .header("X-Usuario-Canal", ECanal.AGENTE_AUTORIZADO)
                .param("colaboradoresIds", "2")
                .param("dataInicio", "20/06/2023")
                .param("dataFim", "29/06/2023")
                .param("agenteAutorizadoId", "1")
                .param("subCanalId", "2"))
            .andExpect(status().isOk());

        verify(service).getCsv(any(), any(), eq(ECanal.AGENTE_AUTORIZADO), eq(1),eq(2));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getColaboradores_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URL + "/colaboradores")
                .header("X-Usuario-Canal", ECanal.AGENTE_AUTORIZADO))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getColaboradores(any(), any(),any());
    }

    @Test
    @SneakyThrows
    public void getColaboradores_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URL + "/colaboradores")
                .header("X-Usuario-Canal", ECanal.AGENTE_AUTORIZADO)
                .param("agenteAutorizadoId", "1"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).getColaboradores(any(), any(),any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_RELATORIO_LOGIN_LOGOUT})
    public void getColaboradores_deveRetornarBadRequest_quandoCanalNaoInformado() {
        mvc.perform(get(API_URL + "/colaboradores")
                .param("agenteAutorizadoId", "1"))
            .andExpect(status().isBadRequest());

        verify(service, never()).getColaboradores(any(), any(),any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_RELATORIO_LOGIN_LOGOUT})
    public void getColaboradores_deveRetornarOk_quandoAgenteAutorizadoIdNaoInformado() {
        mvc.perform(get(API_URL + "/colaboradores")
                .header("X-Usuario-Canal", ECanal.AGENTE_AUTORIZADO))
            .andExpect(status().isOk());

        verify(service).getColaboradores(ECanal.AGENTE_AUTORIZADO, null,null);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {VISUALIZAR_RELATORIO_LOGIN_LOGOUT})
    public void getColaboradores_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URL + "/colaboradores")
                .header("X-Usuario-Canal", ECanal.AGENTE_AUTORIZADO)
                .param("agenteAutorizadoId", "1")
                .param("subCanalId", "2"))
            .andExpect(status().isOk());

        verify(service).getColaboradores(ECanal.AGENTE_AUTORIZADO, 1,2);
    }
}
