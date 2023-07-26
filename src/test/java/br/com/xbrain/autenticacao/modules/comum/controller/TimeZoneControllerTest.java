package br.com.xbrain.autenticacao.modules.comum.controller;

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
import org.springframework.security.test.context.support.WithAnonymousUser;
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
@WebMvcTest(TimeZoneController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
    @MockBean(SubCanalCustomExceptionHandler.class),
})
@Import(OAuth2ResourceConfig.class)
public class TimeZoneControllerTest {

    private static String URI = "/api/timezone";

    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAll_isUnauthorized_quandoNaoPassarAToken() {
        mvc.perform(get(URI)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAll_deveRetornarTodos_quandoAutenticado() {
        mvc.perform(get(URI)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].label", is("Horário do Acre")))
                .andExpect(jsonPath("$[0].value", is("ACT")))
                .andExpect(jsonPath("$[1].label", is("Horário do Amazonas")))
                .andExpect(jsonPath("$[1].value", is("AMT")))
                .andExpect(jsonPath("$[2].label", is("Horário de Brasília")))
                .andExpect(jsonPath("$[2].value", is("BRT")))
                .andExpect(jsonPath("$[3].label", is("Horário de Fernando de Noronha")))
                .andExpect(jsonPath("$[3].value", is("FNT")));
    }
}
