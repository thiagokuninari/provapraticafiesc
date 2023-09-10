package br.com.xbrain.autenticacao.modules.organizacaoempresa.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.service.OrganizacaoEmpresaHistoricoService;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import(OAuth2ResourceConfig.class)
@WebMvcTest(controllers = OrganizacaoEmpresaHistoricoController.class)
public class OrganizacaoEmpresaHistoricoControllerTest {

    private static final String API_URI = "/api/organizacao-empresa-historico";
    private static final String GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO = "VAR_GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private TokenStore tokenStore;
    @MockBean
    private OrganizacaoEmpresaHistoricoService service;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @MockBean
    private UsuarioSubCanalObserver usuarioSubCanalObserver;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getHistoricoDaOrganizacaoEmpresa_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(API_URI + "/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).obterHistoricoDaOrganizacaoEmpresa(any());
    }

    @Test
    @SneakyThrows
    public void getHistoricoDaOrganizacaoEmpresa_deveRetornarForbidden_quandoUsuarioNaoTiverPermissao() {
        mvc.perform(get(API_URI + "/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).obterHistoricoDaOrganizacaoEmpresa(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void getHistoricoDaOrganizacaoEmpresa_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI + "/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).obterHistoricoDaOrganizacaoEmpresa(eq(1));
    }
}
