package br.com.xbrain.autenticacao.modules.oauth;

import helpers.OAuthToken;
import helpers.TestsHelper;
import helpers.Usuarios;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void deveAutenticar() {
        OAuthToken token = TestsHelper.getAccessTokenObject(mvc, Usuarios.ADMIN);
        assertNotNull(token.getAccessToken());
        assertEquals("100-ADMIN@XBRAIN.COM.BR", token.getLogin());
        assertEquals("100", token.getUsuarioId());
        assertEquals("ADMIN@XBRAIN.COM.BR", token.getEmail());
        assertEquals("ADMIN", token.getNome());
        assertEquals("X-BRAIN", token.getNivel());
        assertEquals("Administrador", token.getDepartamento());
        assertEquals("Administrador", token.getCargo());
        assertEquals("XBRAIN", token.getNivelCodigo());
        assertEquals("ADMIN", token.getCargoCodigo());
        assertEquals("ADMIN", token.getDepartamentoCodigo());
        assertEquals("F", token.getAlterarSenha());
        assertEquals("38957979875", token.getCpf());
        assertFalse(token.getAuthorities().isEmpty());
    }

    @Test
    public void deveNaoAutenticar() {
        OAuthToken token = TestsHelper.getAccessTokenObject(mvc, "INVALIDO@XBRAIN.COM.BR");
        assertNull(token.getAccessToken());
    }

    @Test
    public void deveChecarATokenComoValida() throws Exception {
        OAuthToken token = TestsHelper.getAccessTokenObject(mvc, Usuarios.ADMIN);

        mvc.perform(
                post("/oauth/check_token")
                        .param("token", token.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId", is(100)))
                .andExpect(jsonPath("$.nome", is("ADMIN")))
                .andExpect(jsonPath("$.email", is("ADMIN@XBRAIN.COM.BR")))
                .andExpect(jsonPath("$.login", is("100-ADMIN@XBRAIN.COM.BR")))
                .andExpect(jsonPath("$.nivel", is("X-BRAIN")))
                .andExpect(jsonPath("$.departamento", is("Administrador")))
                .andExpect(jsonPath("$.cargo", is("Administrador")))
                .andExpect(jsonPath("$.cargoCodigo", is("ADMIN")))
                .andExpect(jsonPath("$.departamentoCodigo", is("ADMIN")))
                .andExpect(jsonPath("$.nivelCodigo", is("XBRAIN")))
                .andExpect(jsonPath("$.authorities", not(empty())))
                .andExpect(jsonPath("$.cpf", is("38957979875")));
    }

    @Test
    public void deveChecarATokenComoInvalida() throws Exception {
        mvc.perform(
                post("/oauth/check_token")
                        .param("token", "teste"))
                .andExpect(status().isBadRequest());
    }
}
