package br.com.xbrain.autenticacao.modules.comum.controller;

import lombok.SneakyThrows;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class DiasUteisControllerTest {

    private static final String API_URI = "/api/dias-uteis";

    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    public void getDataComDiasUteisAdicionado_deveRetornarUnauthorizied_quandoNaoTiverUsuarioAutenticado() {
        mvc.perform(get(API_URI))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void getDataComDiasUteisAdicionado_deveRetornarBadRequest_quandoFaltarCampoObrigatorio() {
        mvc.perform(get(API_URI)
            .param("cidadeId", "1111")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void getDataComDiasUteisAdicionado_deveRetornarOk_quandoRequestTiverCamposObrigatorios() {
        mvc.perform(get(API_URI)
            .param("cidadeId", "1111")
            .param("dataOriginal", "2020-01-25T21:34:55")
            .param("qtdDiasUteisAdicionar", "3")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
