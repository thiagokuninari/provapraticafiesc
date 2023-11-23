package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.service.UfService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(UfController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
    @MockBean(SubCanalCustomExceptionHandler.class),
})
@Import(OAuth2ResourceConfig.class)
public class UfControllerTest {

    private static String API_UF = "/api/ufs";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UfService ufService;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAll_deveRetornarOk_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_UF)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(ufService, times(1)).findAll(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAll_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(API_UF)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(ufService, times(1)).findAll(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllUfs_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_UF + "/todas")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(ufService, never()).findAll();
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllUfs_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(API_UF + "/todas")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(ufService, times(1)).findAll();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllByRegional_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_UF + "/por-regional")
                .param("regionalId", "1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(ufService, never()).findAllByRegionalId(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllByRegional_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(API_UF + "/por-regional")
                .param("regionalId", "1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(ufService, times(1)).findAllByRegionalId(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllByRegionalComUf_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(API_UF + "/por-regional-com-uf?regionalId=1022")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(ufService, times(1)).findAllByRegionalIdComUf(1022);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllByRegionalComUf_deveRetornarUnauthorized_quandoNaoTemAutorizacao() throws Exception {
        mvc.perform(get(API_UF + "/por-regional-com-uf")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
