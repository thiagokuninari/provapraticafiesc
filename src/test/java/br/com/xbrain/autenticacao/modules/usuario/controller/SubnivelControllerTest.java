package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import br.com.xbrain.autenticacao.modules.usuario.service.SubnivelService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import(OAuth2ResourceConfig.class)
@WebMvcTest(SubnivelController.class)
@MockBeans({
    @MockBean(TokenStore.class),
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(UsuarioSubCanalObserver.class),
})
public class SubnivelControllerTest {

    private static final String URL = "/api/sub-niveis";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private SubnivelService service;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getSubniveisSelect_deveRetornarUnauthorized_quandoUsuarioAutenticado() {
        mvc.perform(get(URL.concat("/select")))
            .andExpect(status().isUnauthorized());

        verifyZeroInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getSubniveisSelect_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(URL.concat("/select"))
                .param("nivelId", "1"))
            .andExpect(status().isOk());

        verify(service).getSubniveisSelect(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getSubniveisSelect_deveRetornarBadRequest_quandoNaoReceberNivelId() {
        mvc.perform(get(URL.concat("/select")))
            .andExpect(status().isBadRequest());

        verifyZeroInteractions(service);
    }
}
