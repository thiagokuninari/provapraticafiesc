package br.com.xbrain.autenticacao.modules.comum.controller;

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
import static helpers.Usuarios.ADMIN;
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
public class RegionalControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void deveSolicitarAutenticacao() throws Exception {
        mvc.perform(get("/api/regionais")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void deveRetornarAsRegionaisAtivas() throws Exception {
        mvc.perform(get("/api/regionais")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(10)))
            .andExpect(jsonPath("$[0].nome", is("RBS")))
            .andExpect(jsonPath("$[1].nome", is("RCO")))
            .andExpect(jsonPath("$[2].nome", is("RMG")))
            .andExpect(jsonPath("$[3].nome", is("RNE")))
            .andExpect(jsonPath("$[4].nome", is("RNO")))
            .andExpect(jsonPath("$[5].nome", is("RPS")))
            .andExpect(jsonPath("$[6].nome", is("RRE")))
            .andExpect(jsonPath("$[7].nome", is("RRS")))
            .andExpect(jsonPath("$[8].nome", is("RSC")))
            .andExpect(jsonPath("$[9].nome", is("RSI")));

    }

    @Test
    public void deveRetornarSomenteAsRegionaisAtivasGerenteComercial() throws Exception {
        mvc.perform(get("/api/regionais")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].nome", is("RPS")));
    }

    @Test
    public void deveRetornarIdsDeNovasRegionais() throws Exception {
        mvc.perform(get("/api/regionais/novas-regionais-ids")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(10)))
            .andExpect(jsonPath("$[0]", is(1022)))
            .andExpect(jsonPath("$[1]", is(1023)))
            .andExpect(jsonPath("$[2]", is(1024)))
            .andExpect(jsonPath("$[3]", is(1025)))
            .andExpect(jsonPath("$[4]", is(1026)))
            .andExpect(jsonPath("$[5]", is(1027)))
            .andExpect(jsonPath("$[6]", is(1028)))
            .andExpect(jsonPath("$[7]", is(1029)))
            .andExpect(jsonPath("$[8]", is(1030)))
            .andExpect(jsonPath("$[9]", is(1031)));

    }
}
