package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.dto.NivelResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.NivelTipoVisualizacao;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.service.NivelService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = NivelController.class)
@AutoConfigureMockMvc
public class NivelControllerTest {

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private NivelService nivelService;
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
    public void get_isUnauthorized_quandoNaoInformarAToken() throws Exception {
        mvc.perform(get("/api/niveis")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getPermitidos_deveRetornarOsNiveis_filtrandoPorTipoDeVisualizacao() throws Exception {
        when(nivelService
                .getPermitidos(eq(NivelTipoVisualizacao.CADASTRO)))
                .thenReturn(Collections.singletonList(
                        Nivel.builder().id(1).nome("Nivel").build()));

        mvc.perform(get("/api/niveis/permitidos/CADASTRO")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("Nivel")));
    }

    @Test
    @WithMockUser
    public void getNivelParaOrganizacao_deveRetornarOsNiveis_filtrandoPorPermitidosParaOrganizacao() throws Exception {
        when(nivelService.getPermitidosParaOrganizacao()).thenReturn(List.of(
            NivelResponse.builder().id(5).nome("VAREJO").codigo(CodigoNivel.VAREJO.name()).build(),
            NivelResponse.builder().id(8).nome("RECEPTIVO").codigo(CodigoNivel.RECEPTIVO.name()).build()));

        mvc.perform(get("/api/niveis/organizacao")
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

    @Test
    @WithAnonymousUser
    public void getByCodigo_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() throws Exception {
        mvc.perform(get("/api/niveis/codigo/BACKOFFICE_CENTRALIZADO")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(nivelService, never()).getByCodigo(any(CodigoNivel.class));
    }

    @Test
    @WithMockUser
    public void getByCodigo_deveRetornarOk_quandoUsuarioAutenticado() throws Exception {
        mvc.perform(get("/api/niveis/codigo/BACKOFFICE_CENTRALIZADO")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(nivelService).getByCodigo(CodigoNivel.BACKOFFICE_CENTRALIZADO);
    }
}
