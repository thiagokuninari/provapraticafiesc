package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.permissao.service.FuncionalidadeService;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(FuncionalidadeController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
    @MockBean(UsuarioSubCanalObserver.class),
})
@Import(OAuth2ResourceConfig.class)
public class FuncionalidadeControllerTest {

    private static final String URL = "/api/funcionalidades";
    private static final String USUARIO_HELPDESK = "USUARIO HELPDESK";
    private static final String USUARIO_ADMIN = "USUARIO ADMIN";
    private static final String CHM_ABRIR_CHAMADO = "CHM_ABRIR_CHAMADO";
    private static final String AUT_VISUALIZAR_USUARIO = "AUT_VISUALIZAR_USUARIO";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private FuncionalidadeService funcionalidadeService;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAll_unauthorized_quandoNaoPassarAToken() {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = USUARIO_HELPDESK, roles = { CHM_ABRIR_CHAMADO })
    public void getAll_forbidden_quandoNaoTiverPermissaoParaControleDeUsuarios() {
        mvc.perform(get(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = USUARIO_ADMIN, roles = { AUT_VISUALIZAR_USUARIO })
    public void getAll_ok_quandoTiverPermissaoParaControleDeUsuarios() {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
