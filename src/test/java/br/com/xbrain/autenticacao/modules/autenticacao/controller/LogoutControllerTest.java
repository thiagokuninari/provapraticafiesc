package br.com.xbrain.autenticacao.modules.autenticacao.controller;

import br.com.xbrain.autenticacao.config.AuthServerConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.OPERACAO_GERENTE_COMERCIAL;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class LogoutControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private TokenStore tokenStore;

    @Test
    public void deveSolicitarAutenticacao() throws Exception  {
        mvc.perform(get("/api/logout")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deveFazerOLogoutDoUsuarioLogado() throws Exception  {
        String token = getAccessToken(mvc, ADMIN);

        requestEmpresas(token).andExpect(status().isOk());

        mvc.perform(get("/api/logout")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        requestEmpresas(token).andExpect(status().isUnauthorized());
    }

    @Test
    public void deveFazerOLogoutDoUsuarioPassadoPorParametro() throws Exception  {
        String token = getAccessToken(mvc, ADMIN);

        requestEmpresas(token).andExpect(status().isOk());

        mvc.perform(get("/api/logout/usuario/100")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        requestEmpresas(token).andExpect(status().isUnauthorized());
    }

    private ResultActions requestEmpresas(String token) throws Exception {
        return mvc.perform(get("/api/empresas")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    public void devePermitirSomenteUsuariosXBrainFazerLogoutDeTodosOsUsuarios() throws Exception  {
        mvc.perform(get("/api/logout/todos-usuarios")
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deveDeslogarTodosOsUsuarios() throws Exception  {
        getAccessToken(mvc, ADMIN);
        getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL);

        assertFalse(tokenStore.findTokensByClientId(AuthServerConfig.APP_CLIENT).isEmpty());

        mvc.perform(get("/api/logout/todos-usuarios")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}