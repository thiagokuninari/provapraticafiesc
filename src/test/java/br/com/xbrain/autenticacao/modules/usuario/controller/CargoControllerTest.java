package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.service.CargoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.CargoHelper.*;
import static helpers.TestsHelper.convertObjectToJsonBytes;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(CargoController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
    @MockBean(UsuarioSubCanalObserver.class),
})
public class CargoControllerTest {

    private static final String API_CARGO = "/api/cargos";

    private MockMvc mvc;
    @MockBean
    private CargoService cargoService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    @WithAnonymousUser
    public void getAll_isUnauthorized_quandoNaoInformarAToken() throws Exception {
        mvc.perform(get(API_CARGO)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getAll_deveRetornarOsCargos_conformeNivelECanaisPermitidosFiltrados() throws Exception {
        when(cargoService.getAll(any(), any()))
            .thenReturn(umCargoPage(1, "Administrador", 4));
        when(cargoService
            .getPermitidosPorNivelECanaisPermitidos(eq(7), eq(Set.of(ECanal.D2D_PROPRIO, ECanal.ATIVO_PROPRIO)), eq(true)))
            .thenReturn(List.of(Cargo.builder()
                .codigo(CodigoCargo.OPERACAO_TECNICO)
                .nome("OPERADOR TECNICO")
                .build()));

        var canaisParam = Stream.of(ECanal.D2D_PROPRIO, ECanal.ATIVO_PROPRIO)
            .map(ECanal::name)
            .collect(Collectors.joining(","));
        mvc.perform(get(API_CARGO)
                .param("nivelId", "7")
                .param("canais", canaisParam)
                .param("permiteEditarCompleto", "true")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$", not(empty())))
            .andExpect(jsonPath("$[0].codigo", is(CodigoCargo.OPERACAO_TECNICO.name())))
            .andExpect(jsonPath("$[0].nome", is("OPERADOR TECNICO")));
    }

    @Test
    @WithMockUser
    public void getAll_deveRetornarCargos_quandoForFiltradoPorNivel() throws Exception {
        when(cargoService.getAll(any(), any()))
            .thenReturn(umCargoPage(1, "Administrador", 4));

        mvc.perform(get(API_CARGO + "/gerencia?nivelId=4")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", not(empty())))
            .andExpect(jsonPath("$.content[0].nome", is("Administrador")));
    }

    @Test
    @WithMockUser
    public void findCargoById_deveRetornarCargo_quandoForPassadoId() throws Exception {
        when(cargoService.findById(any()))
            .thenReturn(umCargoNivelAdministrador(1, "Administrador"));

        mvc.perform(get(API_CARGO + "/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("Administrador")));
    }

    @Test
    @WithMockUser
    public void save_deveRetornarCargo_quandoForSalvo() throws Exception {
        when(cargoService.save(any()))
            .thenReturn(umCargoNivelAdministrador(2, "Administrador"));

        mvc.perform(post(API_CARGO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umCargoRequest(1, "Administrador"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("Administrador")));
    }

    @Test
    @WithMockUser
    public void update_deveRetornarCargo_quandoForAtualizado() throws Exception {
        when(cargoService.update(any()))
            .thenReturn(umCargoNivelAdministrador(2, "Vendedor"));

        mvc.perform(put(API_CARGO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umCargoRequest(2, "Vendedor"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("Vendedor")));
    }

    @Test
    @WithMockUser
    public void situacao_deveRetornarCargo_quandoSituacaoForAlterado() throws Exception {
        when(cargoService.situacao(any()))
            .thenReturn(umCargoNivelAdministrador(2, "Vendedor"));

        mvc.perform(put(API_CARGO + "/altera-situacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umCargoRequest(2, "Vendedor"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("Vendedor")));
    }

    @Test
    @WithMockUser
    public void getAll_deveRetornarOsCargosComNomeNivel_conformeNivelFiltrado() throws Exception {
        when(cargoService.getPermitidosPorNiveis(anyList()))
            .thenReturn(List.of(umCargoVendedor()));

        mvc.perform(get(API_CARGO + "/com-nivel?niveisId=1,2,3")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", not(empty())))
            .andExpect(jsonPath("$[0].nome", is("Vendedor - Xbrain")));
    }

    @Test
    @WithMockUser
    public void getAllCargos_deveRetornarTodosOsCodigosCargos_quandoSolicitado() throws Exception {
        when(cargoService.getAllCargos()).thenReturn(List.of(CodigoCargo.AGENTE_AUTORIZADO_GERENTE,
            CodigoCargo.INTERNET_GERENTE));

        mvc.perform(get(API_CARGO + "/codigo-cargos")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0]", is("AGENTE_AUTORIZADO_GERENTE")))
            .andExpect(jsonPath("$[1]", is("INTERNET_GERENTE")));
    }
}
