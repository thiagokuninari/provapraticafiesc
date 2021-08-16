package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.service.CargoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class CargoControllerTest {
    private static final String API_CARGO = "/api/cargos";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private CargoService cargoService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAll_isUnauthorized_quandoNaoInformarAToken() throws Exception {
        mvc.perform(get(API_CARGO)
            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
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
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", not(empty())))
            .andExpect(jsonPath("$[0].codigo", is(CodigoCargo.OPERACAO_TECNICO.name())))
            .andExpect(jsonPath("$[0].nome", is("OPERADOR TECNICO")));
    }

    @Test
    public void getAll_deveRetornarCargos_quandoForFiltradoPorNivel() throws Exception {
        when(cargoService.getAll(any(), any()))
            .thenReturn(umCargoPage(1, "Administrador", 4));

        mvc.perform(get(API_CARGO + "/gerencia?nivelId=4")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", not(empty())))
            .andExpect(jsonPath("$.content[0].nome", is("Administrador")));
    }

    @Test
    public void findCargoById_deveRetornarCargo_quandoForPassadoId() throws Exception {
        when(cargoService.findById(any()))
            .thenReturn(umCargoNivelAdministrador(1, "Administrador"));

        mvc.perform(get(API_CARGO + "/1")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("Administrador")));
    }

    @Test
    public void save_deveRetornarCargo_quandoForSalvo() throws Exception {
        when(cargoService.save(any()))
            .thenReturn(umCargoNivelAdministrador(2, "Administrador"));

        mvc.perform(post(API_CARGO)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umCargoRequest(1, "Administrador"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("Administrador")));
    }

    @Test
    public void update_deveRetornarCargo_quandoForAtualizado() throws Exception {
        when(cargoService.update(any()))
            .thenReturn(umCargoNivelAdministrador(2, "Vendedor"));

        mvc.perform(put(API_CARGO)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umCargoRequest(2, "Vendedor"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("Vendedor")));
    }

    @Test
    public void situacao_deveRetornarCargo_quandoSituacaoForAlterado() throws Exception {
        when(cargoService.situacao(any()))
            .thenReturn(umCargoNivelAdministrador(2, "Vendedor"));

        mvc.perform(put(API_CARGO + "/altera-situacao")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umCargoRequest(2, "Vendedor"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("Vendedor")));
    }

    public Page<Cargo> umCargoPage(Integer id, String nome, Integer nivelId) {
        return new PageImpl<>(List.of(Cargo
            .builder()
            .id(id)
            .nome(nome)
            .nivel(Nivel
                .builder()
                .id(nivelId)
                .build())
            .build()));
    }

    public Cargo umCargoNivelAdministrador(Integer id, String nome) {
        return Cargo.builder()
            .id(id)
            .nome(nome)
            .nivel(Nivel
                .builder()
                .id(4)
                .nome("Administrador")
                .build())
            .build();
    }

    public CargoRequest umCargoRequest(Integer id, String nome) {
        return CargoRequest
            .builder()
            .id(id)
            .nome(nome)
            .situacao(ESituacao.A)
            .nivel(Nivel
                .builder()
                .id(4)
                .build())
            .build();
    }

    @Test
    public void getAll_deveRetornarOsCargosComNomeNivel_conformeNivelFiltrado() throws Exception {
        when(cargoService.getPermitidosPorNiveis(anyList()))
            .thenReturn(List.of(umCargo()));

        mvc.perform(get("/api/cargos/com-nivel?niveisId=1,2,3")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].nome", is("Vendedor - Xbrain")));
    }

    private Cargo umCargo() {
        return Cargo.builder()
                .nivel(Nivel.builder()
                        .nome("Xbrain")
                        .build())
                .nome("Vendedor")
                .build();
    }
}
