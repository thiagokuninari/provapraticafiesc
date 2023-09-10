package br.com.xbrain.autenticacao.modules.organizacaoempresa.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.service.ModalidadeEmpresaService;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import(OAuth2ResourceConfig.class)
@WebMvcTest(controllers = ModalidadeEmpresaController.class)
public class ModalidadeEmpresaControllerTest {

    private static final String API_URI = "/api/modalidade-empresa";
    private static final String GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO = "VAR_GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private TokenStore tokenStore;
    @MockBean
    private ModalidadeEmpresaService service;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @MockBean
    private UsuarioSubCanalObserver usuarioSubCanalObserver;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarModalidadeEmpresa_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(API_URI)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getAllModalidadeEmpresa();
    }

    @Test
    @SneakyThrows
    public void buscarModalidadeEmpresa_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(API_URI)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).getAllModalidadeEmpresa();
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void buscarModalidadeEmpresa_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URI)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).getAllModalidadeEmpresa();
    }
}
