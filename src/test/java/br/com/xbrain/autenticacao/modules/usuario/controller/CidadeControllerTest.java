package br.com.xbrain.autenticacao.modules.usuario.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
                .andExpect(jsonPath("$", hasSize(399)));
    }

    @Test
    public void deveRetornarTodosPorSubCluster() throws Exception {
        mvc.perform(get("/api/cidades?idSubCluster=682")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].nome", is("ANTONINA")))
                .andExpect(jsonPath("$[0].subCluster.nome", is("BRI - PARANAGUÁ - PR")))
                .andExpect(jsonPath("$[0].subCluster.cluster.nome", is("CURITIBA")))
                .andExpect(jsonPath("$[0].subCluster.cluster.grupo.nome", is("CURITIBA")))
                .andExpect(jsonPath("$[0].subCluster.cluster.grupo.regional.nome", is("SUL")));
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

    @Test
    public void deveRetornarCidadesPorUsuarioId() throws Exception {
        MvcResult result = mvc.perform(get("/api/cidades/usuario/1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }
}
