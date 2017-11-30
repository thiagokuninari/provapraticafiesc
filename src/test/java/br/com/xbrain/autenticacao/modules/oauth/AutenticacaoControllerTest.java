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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void deveAutenticar() {
        OAuthToken token = TestsHelper.getAccessTokenObject(mvc, Usuarios.ADMIN);
        assertNotNull(token.getAccessToken());
        assertEquals("200-ADMIN@XBRAIN.COM.BR", token.getLogin());
        assertFalse(token.getPermissoes().isEmpty());
        assertEquals("ADMIN", token.getNome());
        assertEquals("X-BRAIN", token.getNivel());
        assertEquals("Administrativo", token.getDepartamento());
        assertEquals("ADMINISTRADOR", token.getCargo());
    }

    @Test
    public void deveNaoAutenticar() {
        OAuthToken token = TestsHelper.getAccessTokenObject(mvc, "INVALIDO@XBRAIN.COM.BR");
        assertNull(token.getAccessToken());
    }
}
