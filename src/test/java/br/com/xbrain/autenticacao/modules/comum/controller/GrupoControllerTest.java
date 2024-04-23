package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.service.GrupoService;
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

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(GrupoController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
    @MockBean(SubCanalCustomExceptionHandler.class),
})
@Import(OAuth2ResourceConfig.class)
public class GrupoControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private GrupoService grupoService;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAtivosPorRegional_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get("/api/grupos")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAtivosPorRegional_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get("/api/grupos?regionalId=1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(grupoService).getAllByRegionalId(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAtivosPorRegional_deveRetornarOk_seNaoInformarRegionalId() {
        mvc.perform(get("/api/grupos")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(grupoService).getAllAtiva();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllByRegionalIdAndUsuarioId_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get("/api/grupos/regional/{regionalId}/usuario/{usuarioId}", 10, 100)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllByRegionalIdAndUsuarioId_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get("/api/grupos/regional/{regionalId}/usuario/{usuarioId}", 10, 100)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findById_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get("/api/grupos/{grupoId}", 4)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findById_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get("/api/grupos/{grupoId}", 4)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(grupoService).findById(4);
    }
}
