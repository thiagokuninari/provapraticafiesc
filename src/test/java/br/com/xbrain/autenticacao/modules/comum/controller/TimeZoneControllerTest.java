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

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class TimeZoneControllerTest {

    private static String URL = "/api/timezone";

    @Autowired
    private MockMvc mvc;

    @Test
    public void getAll_isUnauthorized_quandoNaoPassarAToken() throws Exception {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAll_deveRetornarTodos_quandoAutenticado() throws Exception {
        mvc.perform(get(URL)
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(9)))
                .andExpect(jsonPath("$[0].label", is("America/Campo_Grande GMT -4")))
                .andExpect(jsonPath("$[0].value", is("AMERICA_CAMPO_GRANDE")))
                .andExpect(jsonPath("$[1].label", is("America/Cuiaba GMT -4")))
                .andExpect(jsonPath("$[1].value", is("AMERICA_CUIABA")))
                .andExpect(jsonPath("$[2].label", is("America/Maceio GMT -3")))
                .andExpect(jsonPath("$[2].value", is("AMERICA_MACEIO")))
                .andExpect(jsonPath("$[3].label", is("America/Manaus GMT -4")))
                .andExpect(jsonPath("$[3].value", is("AMERICA_MANAUS")))
                .andExpect(jsonPath("$[4].label", is("America/Noronha GMT -2")))
                .andExpect(jsonPath("$[4].value", is("AMERICA_NORONHA")))
                .andExpect(jsonPath("$[5].label", is("America/Porto_Acre GMT -5")))
                .andExpect(jsonPath("$[5].value", is("AMERICA_PORTO_ACRE")))
                .andExpect(jsonPath("$[6].label", is("America/Recife GMT -3")))
                .andExpect(jsonPath("$[6].value", is("AMERICA_RECIFE")))
                .andExpect(jsonPath("$[7].label", is("America/Rio_Branco GMT -5")))
                .andExpect(jsonPath("$[7].value", is("AMERICA_RIO_BRANCO")))
                .andExpect(jsonPath("$[8].label", is("America/Sao_Paulo GMT -3")))
                .andExpect(jsonPath("$[8].value", is("AMERICA_SAO_PAULO")));
    }
}
