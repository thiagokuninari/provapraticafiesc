package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import br.com.xbrain.autenticacao.modules.usuario.service.SupervisorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao.REGIONAL;
import static br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao.UF;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SupervisorController.class)
@AutoConfigureMockMvc
public class SupervisorControllerTest {

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private SupervisorService supervisorService;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @MockBean
    private UsuarioSubCanalObserver usuarioSubCanalObserver;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    @WithAnonymousUser
    public void getAssistentesEVendedores_isUnauthorized_quandoNaoPassarAToken() throws Exception {
        mvc.perform(get("/api/supervisor/assistentes-vendedores/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAnonymousUser
    public void getPorAreaAtuacao_isUnauthorized_quandoNaoPassarAToken() throws Exception {
        mvc.perform(get("/api/supervisor/por-area-atuacao/SUBCLUSTER/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser
    public void getAssistentesEVendedores_deveRetornarOsAssistentesEVendedores_doSupervisorPassado() throws Exception {
        when(supervisorService.getCargosDescendentesEVendedoresD2dDoSupervisor(any(), any(), any()))
            .thenReturn(singletonList(
                UsuarioResponse.builder()
                    .id(1)
                    .nome("VENDEDOR 1")
                    .build()));

        mvc.perform(get("/api/supervisor/assistentes-vendedores/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("VENDEDOR 1")));
    }

    @Test
    @WithMockUser
    public void getAssistentesEVendedores_deveRetornarOsAssistentesEVendedores_doSubCanalIdPassado() throws Exception {
        when(supervisorService.getCargosDescendentesEVendedoresD2dDoSupervisor(any(), any(), any()))
            .thenReturn(singletonList(
                UsuarioResponse.builder()
                    .id(1)
                    .nome("VENDEDOR 1")
                    .build()));

        mvc.perform(get("/api/supervisor/assistentes-vendedores/1")
                .param("subCanalId", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("VENDEDOR 1")));
    }

    @Test
    @WithMockUser
    public void getPorAreaAtuacao_deveRetornarOsSupervisor_conformeAreaDeAtuacaoPorParametro() throws Exception {
        when(supervisorService.getSupervisoresPorAreaAtuacao(any(), any()))
                .thenReturn(singletonList(
                        UsuarioResponse.builder().id(1).nome("VENDEDOR 1").build()));

        mvc.perform(get("/api/supervisor/por-area-atuacao/SUBCLUSTER/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("VENDEDOR 1")));
    }

    @Test
    @WithMockUser
    public void getLideresPorAreaAtuacao_deveRetornarOsSupervisor_conformeAreaDeAtuacaoPorParametro() throws Exception {
        when(supervisorService.getLideresPorAreaAtuacao(any(), any()))
            .thenReturn(singletonList(
                UsuarioResponse.builder().id(1).nome("VENDEDOR 1").build()));

        mvc.perform(get("/api/supervisor/lideres-por-area-atuacao/SUBCLUSTER/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].nome", is("VENDEDOR 1")));
    }

    @Test
    @WithMockUser
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
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].nome", is("RENATO")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].nome", is("JOAO")));
    }

    @Test
    @WithMockUser
    public void getSupervisoresPorAreaAtuacao_deveRetornarOsSupervisores_quandoForNovaRegional() throws Exception {
        when(supervisorService.getSupervisoresPorAreaAtuacao(REGIONAL, List.of(1027)))
            .thenReturn(singletonList(
                UsuarioResponse.builder().id(1).nome("VENDEDOR 1").build()));
        mvc.perform(get("/api/supervisor/por-area-atuacao/REGIONAL/1027")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].nome", is("VENDEDOR 1")));
    }

    @Test
    @WithMockUser
    public void getSupervisoresPorAreaAtuacao_deveRetornarOsSupervisores_quandoPorUf() throws Exception {
        when(supervisorService.getSupervisoresPorAreaAtuacao(UF, List.of(1)))
            .thenReturn(singletonList(
                UsuarioResponse.builder().id(1).nome("SUPERVISOR 1").build()));
        mvc.perform(get("/api/supervisor/por-area-atuacao/UF/1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].nome", is("SUPERVISOR 1")));
    }
}
