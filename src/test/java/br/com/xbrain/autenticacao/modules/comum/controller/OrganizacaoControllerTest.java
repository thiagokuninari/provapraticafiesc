package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.service.OrganizacaoService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(OrganizacaoController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
    @MockBean(SubCanalCustomExceptionHandler.class),
})
@Import(OAuth2ResourceConfig.class)
public class OrganizacaoControllerTest {

    private static final String ORGANIZACOES_API = "/api/organizacoes";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private OrganizacaoService organizacaoService;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllSelect_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(ORGANIZACOES_API + "/select")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(organizacaoService, never()).getAllSelect(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllSelect_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(ORGANIZACOES_API + "/select")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(organizacaoService, times(1)).getAllSelect(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getById_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(ORGANIZACOES_API + "/1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getById_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(ORGANIZACOES_API + "/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
