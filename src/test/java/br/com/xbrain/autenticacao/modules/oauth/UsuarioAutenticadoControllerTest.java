package br.com.xbrain.autenticacao.modules.oauth;

import helpers.Usuarios;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static helpers.TestsHelper.getAccessToken;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class UsuarioAutenticadoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void deveSolicitarAutenticacao() throws Exception {
        mvc.perform(get("/api/usuario-autenticado")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/usuario-autenticado/101")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deveRetornarOUsuarioAutenticado() throws Exception {
        mvc.perform(get("/api/usuario-autenticado")
                .header("Authorization", getAccessToken(mvc, Usuarios.HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(101)))
                .andExpect(jsonPath("$.nome", is("HELPDESK")))
                .andExpect(jsonPath("$.email", is(Usuarios.HELP_DESK)));
    }

    @Test
    public void deveRetornarOUsuarioAutenticadoPorId() throws Exception {
        mvc.perform(get("/api/usuario-autenticado/101")
                .header("Authorization", getAccessToken(mvc, Usuarios.HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(101)));
    }

    @Test
    public void deveRetornarTodasAsCidadesDoUsuario() throws Exception {
        mvc.perform(get("/api/usuario-autenticado/100/cidades")
                .header("Authorization", getAccessToken(mvc, Usuarios.HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("LONDRINA")));
    }

    @Test
    public void deveRetornarNenhumaCidadeParaOUsuario() throws Exception {
        mvc.perform(get("/api/usuario-autenticado/101/cidades")
                .header("Authorization", getAccessToken(mvc, Usuarios.HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
