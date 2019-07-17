package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.service.SupervisorService;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static helpers.TestsHelper.getAccessToken;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
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
public class SupervisorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SupervisorService supervisorService;

    @Test
    public void getAssistentesEVendedores_isUnauthorized_quandoNaoPassarAToken() throws Exception {
        mvc.perform(get("/api/supervisor/assistentes-vendedores/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getPorAreaAtuacao_isUnauthorized_quandoNaoPassarAToken() throws Exception {
        mvc.perform(get("/api/supervisor/por-area-atuacao/SUBCLUSTER/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getAssistentesEVendedores_deveRetornarOsAssistentesEVendedores_doSupervisorPassado() throws Exception {
        when(supervisorService.getAssistentesEVendedoresD2dDoSupervisor(any()))
                .thenReturn(singletonList(
                        UsuarioResponse.builder().id(1).nome("VENDEDOR 1").build()));

        mvc.perform(get("/api/supervisor/assistentes-vendedores/1")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("VENDEDOR 1")));
    }

    @Test
    public void getPorAreaAtuacao_deveRetornarOsSupervisor_conformeAreaDeAtuacaoPorParametro() throws Exception {
        when(supervisorService.getSupervisoresPorAreaAtuacao(any(), any()))
                .thenReturn(singletonList(
                        UsuarioResponse.builder().id(1).nome("VENDEDOR 1").build()));

        mvc.perform(get("/api/supervisor/por-area-atuacao/SUBCLUSTER/1")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("VENDEDOR 1")));
    }

    @Test
    public void getSupervisoresDoSubcluster_deveRetornarSupevisorDoSubCluster_doSupervisorPassado() throws Exception{
        when(supervisorService.getSupervisoresDoSubcluster(any()))
                .thenReturn(singletonList(
                        UsuarioResponse.builder().id(11850).nome("RAFAEL MACHADO DA SILVEIRA").build()));

        mvc.perform(get("/api/supervisor/supervisores/140051")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(11850)))
                .andExpect(jsonPath("$[0].nome", is( "RAFAEL MACHADO DA SILVEIRA")));
    }

}
