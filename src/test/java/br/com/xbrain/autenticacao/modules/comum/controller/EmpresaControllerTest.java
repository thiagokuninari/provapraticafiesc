package br.com.xbrain.autenticacao.modules.comum.controller;

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
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.OPERACAO_GERENTE_COMERCIAL;
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
public class EmpresaControllerTest {

    private static String URL = "/api/empresas";

    @Autowired
    private MockMvc mvc;

    @Test
    public void getAll_isUnauthorized_quandoNaoPassarAToken() throws Exception {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAll_deveRetornarTodos_quandoXbrain() throws Exception {
        mvc.perform(get(URL)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].nome", is("Claro Móvel")))
                .andExpect(jsonPath("$[0].codigo", is("CLARO_MOVEL")));
    }

    @Test
    public void getAll_deveIgnorarXbrain_quandoNaoForXbrain() throws Exception {
        mvc.perform(get(URL)
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void getAll_deveFiltrarPorUnidadeDeNegocio_quandoPassarParametro() throws Exception {
        mvc.perform(get(URL)
                .param("unidadeNegocioId", "1")
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
