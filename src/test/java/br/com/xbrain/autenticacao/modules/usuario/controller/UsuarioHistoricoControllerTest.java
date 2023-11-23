package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

import static helpers.Usuarios.ADMIN;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UsuarioHistoricoController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(UsuarioSubCanalObserver.class),
    @MockBean(TokenStore.class),
})
@Import(OAuth2ResourceConfig.class)
public class UsuarioHistoricoControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UsuarioHistoricoService usuarioHistoricoService;

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getHistoricoDoUsuario_deveBuscarHistoricoDoUsuario_quandoSolicitado() {
        mvc.perform(get("/api/usuario-historico/100")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioHistoricoService).getHistoricoDoUsuario(100);
    }

    @Test
    @SneakyThrows
    public void getHistoricoDoUsuario_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(get("/api/usuario-historico/100")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioHistoricoService);
    }
}
