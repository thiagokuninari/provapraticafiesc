package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.modules.permissao.dto.CargoDepartamentoFuncionalidadeRequest;
import helpers.TestsHelper;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "classpath:/tests_database.sql")
public class CargoDepartamentoFuncionalidadeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void deveSalvar() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/cargo-departamento-funcionalidade")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(novasPermissoes())))
                .andExpect(status().isOk());
    }

    @Test
    public void deveBuscarPermissoes() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/cargo-departamento-funcionalidade")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(552)));
    }

    @Test
    public void devePaginarPermissoes() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/cargo-departamento-funcionalidade/pages?page=0&size=10")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalPages", is(56)))
                .andExpect(jsonPath("$.totalElements", is(552)));
    }

    @Test
    public void deveRemoverUmaPermissao() throws Exception {
        mvc.perform(put("/api/cargo-departamento-funcionalidade/remover/1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deveDeslogarUsuarios() throws Exception {
        mvc.perform(put("/api/cargo-departamento-funcionalidade/deslogar/50/50")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private CargoDepartamentoFuncionalidadeRequest novasPermissoes() {
        CargoDepartamentoFuncionalidadeRequest res = new CargoDepartamentoFuncionalidadeRequest();
        res.setCargoId(1);
        res.setDepartamentoId(1);
        res.setFuncionalidadesIds(Arrays.asList(1, 2, 3, 4));
        return res;
    }
}