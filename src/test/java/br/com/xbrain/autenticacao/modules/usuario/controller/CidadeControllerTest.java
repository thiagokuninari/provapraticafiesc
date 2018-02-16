package br.com.xbrain.autenticacao.modules.usuario.controller;

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
public class CidadeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void deveSolicitarAutenticacao() throws Exception {
        mvc.perform(get("/api/cidades")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deveRetornarTodosPorUf() throws Exception {
        mvc.perform(get("/api/cidades?idUf=1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(401)));
    }

    @Test
    public void deveRetornarCidadePorUfAndCidadeNome() throws Exception {
        mvc.perform(get("/api/cidades/uf-cidade/PR/LONDRINA")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("LONDRINA")));
    }

    @Test
    public void deveRetornarTodosPorSubCluster() throws Exception {
        mvc.perform(get("/api/cidades?idSubCluster=57")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(38)))
                .andExpect(jsonPath("$[0].nome", is("ALHANDRA")))
                .andExpect(jsonPath("$[0].subCluster.nome", is("JOÃO PESSOA")))
                .andExpect(jsonPath("$[0].subCluster.cluster.nome", is("PARAÍBA")))
                .andExpect(jsonPath("$[0].subCluster.cluster.grupo.nome", is("NORDESTE")))
                .andExpect(jsonPath("$[0].subCluster.cluster.grupo.regional.nome", is("LESTE")));
    }

    @Test
    public void deveRetornarTodosPorRegionalId() throws Exception {
        mvc.perform(get("/api/cidades/regional/1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deveRetornarTodosPorGrupoId() throws Exception {
        mvc.perform(get("/api/cidades/grupo/1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deveRetornarTodosPorClusterId() throws Exception {
        mvc.perform(get("/api/cidades/cluster/1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deveRetornarTodosPorSubClusterId() throws Exception {
        mvc.perform(get("/api/cidades/sub-cluster/1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
