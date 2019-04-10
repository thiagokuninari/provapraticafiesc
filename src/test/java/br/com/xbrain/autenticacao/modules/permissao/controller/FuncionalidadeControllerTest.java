package br.com.xbrain.autenticacao.modules.permissao.controller;

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
import static helpers.Usuarios.HELP_DESK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "classpath:/tests_database.sql")
public class FuncionalidadeControllerTest {

    private static final String URL = "/api/funcionalidades";

    @Autowired
    private MockMvc mvc;

    @Test
    public void getAll_unauthorized_quandoNaoPassarAToken() throws Exception {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAll_forbidden_quandoNaoTiverPermissaoParaControleDeUsuarios() throws Exception {
        mvc.perform(get(URL)
                .header("Authorization", getAccessToken(mvc, HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getAll_ok_quandoTiverPermissaoParaControleDeUsuarios() throws Exception {
        mvc.perform(get(URL)
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
