package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.service.UnidadeNegocioService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.exceptions.SubCanalCustomExceptionHandler;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(UnidadeNegocioController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
    @MockBean(SubCanalCustomExceptionHandler.class),
})
@Import(OAuth2ResourceConfig.class)
public class UnidadeNegocioControllerTest {

    private static String URL = "/api/unidades-negocio";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UnidadeNegocioService unidadeNegocioService;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAll_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        Mockito.verify(unidadeNegocioService, never()).getAll();
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAll_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(unidadeNegocioService, times(1)).getAll();
    }
}
