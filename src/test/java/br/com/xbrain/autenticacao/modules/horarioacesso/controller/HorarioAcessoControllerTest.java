package br.com.xbrain.autenticacao.modules.horarioacesso.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.horarioacesso.service.HorarioAcessoService;
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

import static br.com.xbrain.autenticacao.modules.horarioacesso.helper.HorarioHelper.*;
import static helpers.TestsHelper.*;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.OPERACAO_GERENTE_COMERCIAL;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "classpath:/tests_database.sql")
public class HorarioAcessoControllerTest {

    private static final String URL = "/api/horarios-acesso";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private HorarioAcessoService service;

    @Test
    public void getHorariosAcesso_unauthorized_quandoNaoPassarToken() throws Exception {
        mvc.perform(get(URL)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getHorariosAcesso_forbidden_quandoNaoTiverPermissao() throws Exception {
        mvc.perform(get(URL)
            .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getHorariosAcesso_ok_quandoTiverPermissao() throws Exception {
        mvc.perform(get(URL)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void getHorarioAcesso_deveRetornarHorarioAcessoResponse_quandoTiverPermissao() throws Exception {
        when(service.getHorarioAcesso(anyInt())).thenReturn(umHorarioAcessoResponse());

        mvc.perform(get(URL + "/1")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umHorarioAcessoResponse())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.horarioAcessoId", is(1)))
            .andExpect(jsonPath("$.siteId", is(100)))
            .andExpect(jsonPath("$.siteNome", is("SITE TESTE")));
    }

    @Test
    public void getHorarios_deveRetornarListaHorarioAcessoResponse_quandoTiverPermissao() throws Exception {
        when(service.getHorariosAcesso(any(PageRequest.class), any(HorarioAcessoFiltros.class)))
            .thenReturn(umaListaHorarioAcessoResponse());

        mvc.perform(get(URL)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umaListaHorarioAcessoResponse())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].horarioAcessoId", is(1)))
            .andExpect(jsonPath("$.content[0].siteId", is(100)))
            .andExpect(jsonPath("$.content[0].siteNome", is("SITE TESTE")))
            .andExpect(jsonPath("$.content[0].dataAlteracao", is("22/11/2021 13:53:10")))
            .andExpect(jsonPath("$.content[0].usuarioAlteracaoNome", is("USUARIO TESTE")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[0].id", is(1)))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[0].diaSemana", is("Segunda-Feira")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[0].horarioInicio", is("09:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[0].horarioFim", is("15:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[1].id", is(2)))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[1].diaSemana", is("Quarta-Feira")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[1].horarioInicio", is("09:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[1].horarioFim", is("15:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[2].id", is(3)))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[2].diaSemana", is("Sexta-Feira")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[2].horarioInicio", is("09:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[2].horarioFim", is("15:00")));
    }

    @Test
    public void getHistoricos_deveRetornarListaHorarioAcessoResponse_quandoTiverPermissao() throws Exception {
        when(service.getHistoricos(any(PageRequest.class), anyInt())).thenReturn(umaListaHorarioHistoricoResponse());

        mvc.perform(get(URL + "/1/historico")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umaListaHorarioHistoricoResponse())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].horarioAcessoId", is(1)))
            .andExpect(jsonPath("$.content[0].horarioHistoricoId", is(1)))
            .andExpect(jsonPath("$.content[0].siteId", is(100)))
            .andExpect(jsonPath("$.content[0].siteNome", is("SITE TESTE")))
            .andExpect(jsonPath("$.content[0].dataAlteracao", is("22/11/2021 13:53:10")))
            .andExpect(jsonPath("$.content[0].usuarioAlteracaoNome", is("USUARIO TESTE")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[0].id", is(1)))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[0].diaSemana", is("Segunda-Feira")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[0].horarioInicio", is("09:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[0].horarioFim", is("15:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[1].id", is(2)))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[1].diaSemana", is("Quarta-Feira")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[1].horarioInicio", is("09:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[1].horarioFim", is("15:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[2].id", is(3)))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[2].diaSemana", is("Sexta-Feira")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[2].horarioInicio", is("09:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[2].horarioFim", is("15:00")));
    }

    private Page<HorarioAcessoResponse> umaListaHorarioAcessoResponse() {
        return new PageImpl<>(List.of(umHorarioAcessoResponse()));
    }

    private Page<HorarioAcessoResponse> umaListaHorarioHistoricoResponse() {
        return new PageImpl<>(List.of(umHorarioHistoricoResponse()));
    }

}
