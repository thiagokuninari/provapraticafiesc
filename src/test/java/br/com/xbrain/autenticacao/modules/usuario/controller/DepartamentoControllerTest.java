package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import br.com.xbrain.autenticacao.modules.usuario.service.DepartamentoService;
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

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(DepartamentoController.class)
@MockBeans({
    @MockBean(UsuarioSubCanalObserver.class),
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class)})
@Import(OAuth2ResourceConfig.class)
public class DepartamentoControllerTest {

    private static final String BASE_URL = "/api/departamentos";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private DepartamentoService departamentoService;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void get_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void get_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL)
                .param("nivelId", "1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(departamentoService).getPermitidosPorNivel(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getByCargoId_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/cargo-id")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getByCargoId_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL + "/cargo-id")
                .param("cargoId", "2")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(departamentoService).getPermitidosPorCargo(2);
    }
}
