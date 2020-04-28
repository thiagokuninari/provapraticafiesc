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
import static helpers.Usuarios.OPERACAO_GERENTE_COMERCIAL;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class TimeZoneControllerTest {

    private static String URI = "/api/timezone";

    @Autowired
    private MockMvc mvc;

    @Test
    public void getAll_isUnauthorized_quandoNaoPassarAToken() throws Exception {
        mvc.perform(get(URI)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAll_deveRetornarTodos_quandoAutenticado() throws Exception {
        mvc.perform(get(URI)
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].label", is("Horário do Acre")))
                .andExpect(jsonPath("$[0].value", is("ACT")))
                .andExpect(jsonPath("$[1].label", is("Horário do Amazonas")))
                .andExpect(jsonPath("$[1].value", is("AMT")))
                .andExpect(jsonPath("$[2].label", is("Horário de Brasília")))
                .andExpect(jsonPath("$[2].value", is("BRT")))
                .andExpect(jsonPath("$[3].label", is("Horário de Fernando de Noronha")))
                .andExpect(jsonPath("$[3].value", is("FNT")));
    }
}
