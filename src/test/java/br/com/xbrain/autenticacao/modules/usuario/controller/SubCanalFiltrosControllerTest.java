package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.exceptions.SubCanalCustomExceptionHandler;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import(OAuth2ResourceConfig.class)
@WebMvcTest(SubCanalFiltrosController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
    @MockBean(SubCanalCustomExceptionHandler.class),
})
public class SubCanalFiltrosControllerTest {

    private static String URL_BASE = "/api/sub-canais/filtros";

    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    @WithMockUser
    public void getTipoCanalFiltros_deveRetornarLabelAndValueTiposCanais_quandoOk() {
        mvc.perform(get(URL_BASE + "/canais")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(5)))
            .andExpect(jsonPath("$[0].label", is("PAP")))
            .andExpect(jsonPath("$[0].value", is("PAP")))
            .andExpect(jsonPath("$[1].label", is("PAP PME")))
            .andExpect(jsonPath("$[1].value", is("PAP_PME")))
            .andExpect(jsonPath("$[2].label", is("PAP PREMIUM")))
            .andExpect(jsonPath("$[2].value", is("PAP_PREMIUM")))
            .andExpect(jsonPath("$[3].label", is("INSIDE SALES PME")))
            .andExpect(jsonPath("$[3].value", is("INSIDE_SALES_PME")))
            .andExpect(jsonPath("$[4].label", is("PAP CONDOMINIO")))
            .andExpect(jsonPath("$[4].value", is("PAP_CONDOMINIO")));
    }

    @Test
    @SneakyThrows
    public void getTipoCanalFiltros_deveRetornarUnauthorized_quandoUsuarioNaoLogado() {
        mvc.perform(get(URL_BASE + "/canais")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getSubCanalSituacaoFiltros_deveRetornarLabelAndValueSubCanaisSituacoes_quandoOk() {
        mvc.perform(get(URL_BASE + "/situacoes")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].label", is("ATIVO")))
            .andExpect(jsonPath("$[0].value", is("A")))
            .andExpect(jsonPath("$[1].label", is("INATIVO")))
            .andExpect(jsonPath("$[1].value", is("I")));
    }

    @Test
    @SneakyThrows
    public void getSubCanalSituacaoFiltros_deveRetornarUnauthorized_quandoUsuarioNaoLogado() {
        mvc.perform(get(URL_BASE + "/situacoes")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
