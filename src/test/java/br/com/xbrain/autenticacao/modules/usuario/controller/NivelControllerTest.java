package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.NivelResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.NivelTipoVisualizacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.service.NivelService;
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
import java.util.List;

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
public class NivelControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private NivelService nivelService;

    @Test
    public void get_isUnauthorized_quandoNaoInformarAToken() throws Exception {
        mvc.perform(get("/api/niveis")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getPermitidos_deveRetornarOsNiveis_filtrandoPorTipoDeVisualizacao() throws Exception {
        when(nivelService
                .getPermitidos(eq(NivelTipoVisualizacao.CADASTRO)))
                .thenReturn(Collections.singletonList(
                        Nivel.builder().id(1).nome("Nivel").build()));

        mvc.perform(get("/api/niveis/permitidos/CADASTRO")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("Nivel")));
    }

    @Test
    public void getNivelParaOrganizacao_deveRetornarOsNiveis_filtrandoPorPermitidosParaOrganizacao() throws Exception {
        when(nivelService.getPermitidosParaOrganizacao()).thenReturn(List.of(
            NivelResponse.builder().id(5).nome("VAREJO").codigo(CodigoNivel.VAREJO.name()).build(),
            NivelResponse.builder().id(8).nome("RECEPTIVO").codigo(CodigoNivel.RECEPTIVO.name()).build()));

        mvc.perform(get("/api/niveis/organizacao")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(5)))
            .andExpect(jsonPath("$[0].nome", is("VAREJO")))
            .andExpect(jsonPath("$[0].codigo", is("VAREJO")))
            .andExpect(jsonPath("$[1].id", is(8)))
            .andExpect(jsonPath("$[1].nome", is("RECEPTIVO")))
            .andExpect(jsonPath("$[1].codigo", is("RECEPTIVO")));
    }
}
