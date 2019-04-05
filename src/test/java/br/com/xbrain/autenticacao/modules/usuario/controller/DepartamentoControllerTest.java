package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.service.DepartamentoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class DepartamentoControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private DepartamentoService departamentoService;

    @Test
    public void get_isUnauthorized_quandoNaoInformarAToken() throws Exception {
        mvc.perform(get("/api/departamentos")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void get_deveRetornarOsDepartamentos_filtrandoPorNivel() throws Exception {
        when(departamentoService
                .getPermitidosPorNivel(eq(4)))
                .thenReturn(Collections.singletonList(
                        Departamento.builder().id(1).nome("Departamento").build()));

        mvc.perform(get("/api/departamentos?nivelId=4")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("Departamento")));
    }
}
