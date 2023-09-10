package br.com.xbrain.autenticacao.modules.usuarioacesso.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import br.com.xbrain.autenticacao.modules.usuarioacesso.service.UsuarioAcessoService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static br.com.xbrain.autenticacao.modules.usuarioacesso.helper.UsuarioAcessoHelper.umUsuarioLogadoRequest;
import static br.com.xbrain.autenticacao.modules.usuarioacesso.helper.UsuarioAcessoHelper.umaListaPaLogadoDto;
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
@WebMvcTest(controllers = UsuarioAcessoController.class)
public class UsuarioAcessoControllerTest {

    private static final String ENDPOINT_USUARIO_ACESSO = "/api/usuario-acesso";
    private static final String INATIVAR_USUARIOS_SEM_ACESSO = "AUT_INATIVAR_USUARIOS_SEM_ACESSO";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private TokenStore tokenStore;
    @MockBean
    private UsuarioAcessoService service;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @MockBean
    private UsuarioSubCanalObserver usuarioSubCanalObserver;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void inativarUsuariosSemAcesso_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(ENDPOINT_USUARIO_ACESSO + "/inativar"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).inativarUsuariosSemAcesso(any());
    }

    @Test
    @SneakyThrows
    public void inativarUsuariosSemAcesso_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(ENDPOINT_USUARIO_ACESSO + "/inativar")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).inativarUsuariosSemAcesso(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {INATIVAR_USUARIOS_SEM_ACESSO})
    public void inativarUsuariosSemAcesso_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(ENDPOINT_USUARIO_ACESSO + "/inativar")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).inativarUsuariosSemAcesso(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void deletarHistoricoUsuarioAcesso_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(MockMvcRequestBuilders.delete(ENDPOINT_USUARIO_ACESSO + "/historico"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).deletarHistoricoUsuarioAcesso();
    }

    @Test
    @SneakyThrows
    public void deletarHistoricoUsuarioAcesso_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(MockMvcRequestBuilders.delete(ENDPOINT_USUARIO_ACESSO + "/historico")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).deletarHistoricoUsuarioAcesso();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void filtrar_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(ENDPOINT_USUARIO_ACESSO))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getAll(any(), any());
    }

    @Test
    @SneakyThrows
    public void filtrar_deveRetornarBadRequest_quandoDadosObrigatoriosNaoInformado() {
        mvc.perform(get(ENDPOINT_USUARIO_ACESSO)
                .accept(MediaType.ALL_VALUE))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo dataFim é obrigatório.",
                "O campo dataInicio é obrigatório.",
                "O campo tipo é obrigatório.")));

        verify(service, never()).getAll(any(), any());
    }

    @Test
    @SneakyThrows
    public void filtrar_deveRetornarBadRequest_quandoFiltroComPeriodoMaiorQue30Dias() {
        mvc.perform(get(ENDPOINT_USUARIO_ACESSO)
                .param("dataInicio", "01/01/2020")
                .param("dataFim", "10/02/2020")
                .param("tipo", "LOGIN")
                .accept(MediaType.ALL_VALUE))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O período não deve ser superior a 30 dias.")));

        verify(service, never()).getAll(any(), any());
    }

    @Test
    @SneakyThrows
    public void filtrar_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(ENDPOINT_USUARIO_ACESSO)
                .param("dataInicio", "01/01/2020")
                .param("dataFim", "10/01/2020")
                .param("tipo", "LOGIN")
                .accept(MediaType.ALL_VALUE))
            .andExpect(status().isOk());

        verify(service).getAll(any(), any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void exportRegistrosToCsv_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(ENDPOINT_USUARIO_ACESSO + "/relatorio"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).exportRegistrosToCsv(any(), any());
    }

    @Test
    @SneakyThrows
    public void exportRegistrosToCsv_deveRetornarBadRequest_quandoDadosObrigatoriosNaoInformado() {
        mvc.perform(get(ENDPOINT_USUARIO_ACESSO + "/relatorio")
                .accept(MediaType.ALL_VALUE))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo dataFim é obrigatório.",
                "O campo dataInicio é obrigatório.",
                "O campo tipo é obrigatório.")));

        verify(service, never()).exportRegistrosToCsv(any(), any());
    }

    @Test
    @SneakyThrows
    public void exportRegistrosToCsv_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(ENDPOINT_USUARIO_ACESSO + "/relatorio")
                .param("dataInicio", "01/01/2020")
                .param("dataFim", "10/01/2020")
                .param("tipo", "LOGIN")
                .accept(MediaType.ALL_VALUE))
            .andExpect(status().isOk());

        verify(service).exportRegistrosToCsv(any(), any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getTotalUsuariosLogadosPorPeriodo_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(ENDPOINT_USUARIO_ACESSO + "usuarios-logados/por-periodo"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getTotalUsuariosLogadosPorPeriodoByFiltros(any());
    }

    @Test
    @SneakyThrows
    public void getTotalUsuariosLogadosPorPeriodo_deveRetornarOK_quandoDadosValidos() {
        when(service.getTotalUsuariosLogadosPorPeriodoByFiltros(umUsuarioLogadoRequest()))
            .thenReturn(umaListaPaLogadoDto());

        mvc.perform(post(ENDPOINT_USUARIO_ACESSO + "/usuarios-logados/por-periodo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioLogadoRequest())))
            .andExpect(status().isOk());

        verify(service).getTotalUsuariosLogadosPorPeriodoByFiltros(umUsuarioLogadoRequest());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosLogadosAtual_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(ENDPOINT_USUARIO_ACESSO + "/usuarios-logados")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getUsuariosLogadosAtualPorIds(any());
    }

    @Test
    @SneakyThrows
    public void getUsuariosLogadosAtual_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(ENDPOINT_USUARIO_ACESSO + "/usuarios-logados")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).getUsuariosLogadosAtualPorIds(any());
    }
}
