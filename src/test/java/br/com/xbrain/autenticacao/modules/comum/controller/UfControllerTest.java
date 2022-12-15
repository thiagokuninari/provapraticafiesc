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
public class UfControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void deveRetornarTodos() throws Exception {
        mvc.perform(get("/api/ufs")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(27)))
                .andExpect(jsonPath("$[0].nome", is("ACRE")))
                .andExpect(jsonPath("$[1].nome", is("ALAGOAS")));
    }

    @Test
    public void getAllUfs_listaUfs_quandoBuscarTodosAsUfs() throws Exception {
        mvc.perform(get("/api/ufs/todas")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(27)))
                .andExpect(jsonPath("$[0].value").value(15))
                .andExpect(jsonPath("$[0].label").value("ACRE"))
                .andExpect(jsonPath("$[1].value").value(11))
                .andExpect(jsonPath("$[1].label").value("ALAGOAS"));
    }

    @Test
    public void getAllByRegionalComUf_listaUfs_quandoBuscarTodasAsUfsComRegional() throws Exception {
        mvc.perform(get("/api/ufs/por-regional-com-uf?regionalId=1022")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(10))
            .andExpect(jsonPath("$[0].uf").value("BA"))
            .andExpect(jsonPath("$[0].nome").value("BAHIA"))
            .andExpect(jsonPath("$[1].id").value(16))
            .andExpect(jsonPath("$[1].uf").value("SE"))
            .andExpect(jsonPath("$[1].nome").value("SERGIPE"));
    }

    @Test
    public void getAllByRegionalComUf_deveRetornarUnauthorized_quandoNaoTemAutorizacao() throws Exception {
        mvc.perform(get("/api/ufs/por-regional-com-uf")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllByRegionalComUf_deveRetornarBadRequest_quandoParametroErrado() throws Exception {
        mvc.perform(get("/api/ufs/por-regional-com-uf")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }
}
