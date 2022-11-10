package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.service.SubCanalService;
import lombok.SneakyThrows;
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

import java.util.List;

import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
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
public class SubCanalControllerTest {
    private static final String API_URI = "/api/sub-canais";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private SubCanalService subCanalService;

    @Test
    public void getAllSubCanais_deveRetornarOsSubCanais_quandoSolicitado() throws Exception {
        when(subCanalService.getAll()).thenReturn(List.of(
            new SubCanalDto(1, ETipoCanal.PAP, "PAP", ESituacao.A),
            new SubCanalDto(2, ETipoCanal.PAP_PME, "PAP PME", ESituacao.A)));

        mvc.perform(get(API_URI)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].codigo", is("PAP")))
            .andExpect(jsonPath("$[0].nome", is("PAP")))
            .andExpect(jsonPath("$[0].situacao", is("A")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].codigo", is("PAP_PME")))
            .andExpect(jsonPath("$[1].nome", is("PAP PME")))
            .andExpect(jsonPath("$[1].situacao", is("A")));
    }

    @Test
    public void getAllSubCanalById_deveRetornarSubCanal_quandoExistir() throws Exception {
        when(subCanalService.getSubCanalById(anyInt())).thenReturn(
            new SubCanalDto(2, ETipoCanal.PAP_PME, "PAP PME", ESituacao.A));

        mvc.perform(get(API_URI + "/2")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(2)))
            .andExpect(jsonPath("$.codigo", is("PAP_PME")))
            .andExpect(jsonPath("$.nome", is("PAP PME")))
            .andExpect(jsonPath("$.situacao", is("A")));
    }

    @Test
    public void getAllSubCanaisExcetoInsideSalesPme_deveRetornarOsSubCanais_quandoSolicitado() throws Exception {
        when(subCanalService.getAllExcetoInsideSalesPme()).thenReturn(List.of(
            new SubCanalDto(1, ETipoCanal.PAP, "PAP", ESituacao.A),
            new SubCanalDto(2, ETipoCanal.PAP_PME, "PAP PME", ESituacao.A),
            new SubCanalDto(3, ETipoCanal.PAP_PREMIUM, "PAP PREMIUM", ESituacao.A)));

        mvc.perform(get(API_URI + "/exceto-inside-sales")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].codigo", is("PAP")))
            .andExpect(jsonPath("$[0].nome", is("PAP")))
            .andExpect(jsonPath("$[0].situacao", is("A")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].codigo", is("PAP_PME")))
            .andExpect(jsonPath("$[1].nome", is("PAP PME")))
            .andExpect(jsonPath("$[1].situacao", is("A")))
            .andExpect(jsonPath("$[2].id", is(3)))
            .andExpect(jsonPath("$[2].codigo", is("PAP_PREMIUM")))
            .andExpect(jsonPath("$[2].nome", is("PAP PREMIUM")))
            .andExpect(jsonPath("$[2].situacao", is("A")));
    }

    @Test
    @SneakyThrows
    public void getAllSubCanaisExcetoInsideSalesPme_deveRetornarUnauthorized_seNaoAutenticado() {

        mvc.perform(get(API_URI + "/exceto-inside-sales")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
