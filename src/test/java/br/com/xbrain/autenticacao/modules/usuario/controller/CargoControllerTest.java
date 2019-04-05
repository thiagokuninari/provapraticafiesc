package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.service.CargoService;
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

import java.util.List;

import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.*;
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
public class CargoControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private CargoService cargoService;

    @Test
    public void getAll_isUnauthorized_quandoNaoInformarAToken() throws Exception {
        mvc.perform(get("/api/cargos")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAll_deveRetornarOsCargos_conformeNivelFiltrado() throws Exception {
        when(cargoService.getPermitidosPorNivel(eq(1)))
                .thenReturn(List.of(Cargo.builder().nome("teste").build()));

        mvc.perform(get("/api/cargos?nivelId=1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].nome", is("teste")));
    }
}