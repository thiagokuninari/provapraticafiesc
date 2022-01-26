package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
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

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao.REGIONAL;
import static br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao.UF;
import static helpers.TestsHelper.getAccessToken;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.*;
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

    @MockBean
    private AutenticacaoService autenticacaoService;

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
        when(supervisorService.getCargosDescendentesEVendedoresD2dDoSupervisor(any(), any()))
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
    public void getSupervisoresDoSubclusterDoUsuarioPeloCanal_200_quandoBuscarSupervisoresPeloIdECanal() throws Exception {
        when(supervisorService.getSupervisoresDoSubclusterDoUsuarioPeloCanal(1, ECanal.ATIVO_PROPRIO))
            .thenReturn(List.of(
                UsuarioNomeResponse.builder()
                    .id(1)
                    .nome("RENATO")
                    .build(),
                UsuarioNomeResponse.builder()
                    .id(2)
                    .nome("JOAO")
                    .build()
            ));

        mvc.perform(get("/api/supervisor/subcluster/usuario/1/canal/ATIVO_PROPRIO")
            .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].nome", is("RENATO")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].nome", is("JOAO")));
    }

    @Test
    public void getSupervisoresPorAreaAtuacao_deveRetornarOsSupervisores_quandoForNovaRegional() throws Exception {
        when(supervisorService.getSupervisoresPorAreaAtuacao(REGIONAL, List.of(1027)))
            .thenReturn(singletonList(
                UsuarioResponse.builder().id(1).nome("VENDEDOR 1").build()));
        mvc.perform(get("/api/supervisor/por-area-atuacao/REGIONAL/1027")
            .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].nome", is("VENDEDOR 1")));
    }

    @Test
    public void getSupervisoresPorAreaAtuacao_deveRetornarOsSupervisores_quandoPorUf() throws Exception {
        when(supervisorService.getSupervisoresPorAreaAtuacao(UF, List.of(1)))
            .thenReturn(singletonList(
                UsuarioResponse.builder().id(1).nome("SUPERVISOR 1").build()));
        mvc.perform(get("/api/supervisor/por-area-atuacao/UF/1")
            .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].nome", is("SUPERVISOR 1")));
    }
}
