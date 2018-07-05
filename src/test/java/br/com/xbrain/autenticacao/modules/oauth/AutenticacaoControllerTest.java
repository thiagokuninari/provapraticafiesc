package br.com.xbrain.autenticacao.modules.oauth;

import br.com.xbrain.autenticacao.modules.autenticacao.repository.OAuthAccessTokenRepository;
import helpers.OAuthToken;
import helpers.TestsHelper;
import helpers.Usuarios;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    @MockBean
    private OAuthAccessTokenRepository tokenRepository;

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
        assertEquals("ADMINISTRADOR", token.getCargoCodigo());
        assertEquals("ADMINISTRADOR", token.getDepartamentoCodigo());
        assertEquals("F", token.getAlterarSenha());
        assertEquals("38957979875", token.getCpf());
        assertFalse(token.getAuthorities().isEmpty());
        assertEquals(singletonList(4), token.getEmpresas());
        assertEquals(singletonList(3), token.getUnidadesNegocios());
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
                .andExpect(jsonPath("$.cargoCodigo", is("ADMINISTRADOR")))
                .andExpect(jsonPath("$.departamentoCodigo", is("ADMINISTRADOR")))
                .andExpect(jsonPath("$.nivelCodigo", is("XBRAIN")))
                .andExpect(jsonPath("$.empresas", not(empty())))
                .andExpect(jsonPath("$.empresasNome", not(empty())))
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

    @Test
    public void deveAutenticarUsandoClientCredentials() {
        assertNotNull(TestsHelper
                .getAccessTokenClientCredentials(mvc, "parceiros-online-api:p4rc31r0s$p1").getAccessToken());

        assertNotNull(TestsHelper
                .getAccessTokenClientCredentials(mvc, "vendas-api:v3nd4s4p1").getAccessToken());
        
        assertNotNull(TestsHelper
                .getAccessTokenClientCredentials(mvc, "mailing-api:m41l1ng4p1").getAccessToken());
    }

    @Test
    public void deveNaoAutenticarUsandoClientCredentialsQuandoSenhaInvalida() {
        OAuthToken token = TestsHelper.getAccessTokenClientCredentials(mvc, "parceiros-online-api:invalida");
        assertNull(token.getAccessToken());
    }

    @Test
    public void deveRetornarErroQuandoOUsuarioEstiverInativo() throws Exception {
        MockHttpServletResponse response = TestsHelper.getTokenResponse(mvc, Usuarios.INATIVO);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains(
                "Usu&aacute;rio Inativo, solicite a ativa&ccedil;&atilde;o ao seu respons&aacute;vel."));
    }

    @Test
    public void deveRetornarErroQuandoOUsuarioEstiverPendente() throws Exception {
        MockHttpServletResponse response = TestsHelper.getTokenResponse(mvc, Usuarios.PENDENTE);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains(
                "Agente Autorizado com aceite de contrato pendente."));
    }

    @Test
    public void deveDeslogarUsuariosLogadosComOMesmoLogin() {
        TestsHelper.getAccessTokenObject(mvc, Usuarios.ADMIN);
        verify(tokenRepository, times(1)).deleteTokenByUsername(eq("100-ADMIN@XBRAIN.COM.BR"));
    }
}
