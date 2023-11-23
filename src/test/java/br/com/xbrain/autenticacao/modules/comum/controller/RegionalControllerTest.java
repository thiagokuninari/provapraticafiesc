package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.service.RegionalService;
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
@WebMvcTest(RegionalController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
    @MockBean(SubCanalCustomExceptionHandler.class),
})
@Import(OAuth2ResourceConfig.class)
public class RegionalControllerTest {

    private static String API_REGIONAL = "/api/regionais";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private RegionalService service;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAtivos_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_REGIONAL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(service, never()).getAll();
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAtivos_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(API_REGIONAL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).getAll();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAtivosParaComunicados_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_REGIONAL + "/comunicados")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(service, never()).getAtivosParaComunicados();
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAtivosParaComunicados_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(API_REGIONAL + "/comunicados")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service, times(1)).getAtivosParaComunicados();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllByUsuarioId_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_REGIONAL + "/usuario/{id}", 100)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(service, never()).getAllByUsuarioId(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllByUsuarioId_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(API_REGIONAL + "/usuario/{id}", 100)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service, times(1)).getAllByUsuarioId(100);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findById_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_REGIONAL + "/{regionalId}", 1)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(service, never()).findById(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findById_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(API_REGIONAL + "/{regionalId}", 1)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service, times(1)).findById(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getNovasRegionaisIds_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_REGIONAL + "/novas-regionais-ids")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(service, never()).getNovasRegionaisIds();
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getNovasRegionaisIds_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(API_REGIONAL + "/novas-regionais-ids")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service, times(1)).getNovasRegionaisIds();
    }
}
