package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import helpers.Usuarios;
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
public class SubClusterControllerTest {

    private static String API_SUBCLUSTER = "/api/subclusters";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @MockBean
    private SiteService siteService;

    @Test
    public void deveSolicitarAutenticacao() throws Exception {
        mvc.perform(get("/api/subclusters")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deveRetornarOsSubClustersAtivosPorCluster() throws Exception {
        mvc.perform(get("/api/subclusters?clusterId=16")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].nome", is("BRI - ARAPIRACA - AL")));
    }

    @Test
    public void deveRetornarSomenteOsSubClustersAtivosPorClusterGerenteOperacao() throws Exception {
        mvc.perform(get("/api/subclusters?clusterId=45")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("LONDRINA - Claro")));
    }

    @Test
    public void deveRetornarOsSubClustersAtivos() throws Exception {
        mvc.perform(get("/api/subclusters")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(245)))
                .andExpect(jsonPath("$[0].nome", is("ABCDM")));
    }

    @Test
    public void getById_deveRetornar_quandoORequestForValido() throws Exception {
        mvc.perform(get(API_SUBCLUSTER + "/45")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(45)));
    }

    @Test
    public void getById_deveRetornarNotFound_quandoOSubClusterNaoExistir() throws Exception {
        mvc.perform(get(API_SUBCLUSTER + "/999")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getById_deveRetornarUnauthorized_quandoNaoInformarToken() throws Exception {
        mvc.perform(get(API_SUBCLUSTER + "/45"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllSubClustersDoUsuarioAutenticado_deveRetornarLista_quandoUsuarioCidadeCadastradas() throws Exception {
        mvc.perform(get(API_SUBCLUSTER + "/usuario-autenticado")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_SUPERVISOR))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(11)));
    }

    @Test
    public void getAllSubClustersDoUsuarioAutenticado_deveRetornarLista_quandoUsuarioPossuiPermissaoVisuazarGeral()
            throws Exception {
        mvc.perform(get(API_SUBCLUSTER + "/usuario-autenticado")
                .header("Authorization", getAccessToken(mvc, Usuarios.MSO_ANALISTAADM_CLAROMOVEL_PESSOAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(245)))
                .andExpect(jsonPath("$[0].nome", is("ABCDM")));
    }
}
