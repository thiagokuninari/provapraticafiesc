package br.com.xbrain.autenticacao.modules.usuarioacesso.controller;

import lombok.SneakyThrows;
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

import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "classpath:/tests_database.sql")
public class RelatorioLoginLogoutControllerTest {

    private static final String ENDPOINT = "/api/relatorio-login-logout";

    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    public void getLoginsLogoutsDeHoje_deveValidarOsCampos_quandoFiltrosInvalidos() {
        mvc.perform(get(ENDPOINT + "/hoje")
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo colaboradoresIds é obrigatório.")));
    }

    @Test
    @SneakyThrows
    public void getCsv_deveValidarOsCampos_quandoFiltrosInvalidos() {
        mvc.perform(get(ENDPOINT + "/csv")
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo colaboradoresIds é obrigatório.",
                "O campo dataInicio é obrigatório.",
                "O campo dataFim é obrigatório.")));
    }
}
