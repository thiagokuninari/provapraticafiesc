package br.com.xbrain.autenticacao.modules.feriado.controller;

import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.NestedServletException;

import java.time.LocalDate;

import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class FeriadoControllerTest {

    private static final String URL_BASE = "/api/feriado";
    private static final String URL_GERENCIAR = "/api/feriado/gerenciar";

    @SpyBean
    private FeriadoService service;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private DataHoraAtual dataHoraAtual;

    @Test
    public void deveConsultarEncontrarFeriadoPelaData() throws Exception {
        mvc.perform(get(URL_BASE + "/consulta?data=07/09/2018")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(Boolean.TRUE)));
    }

    @Test
    public void deveConsultarNaoEncontrarFeriadoPelaData() throws Exception {
        mvc.perform(get(URL_BASE + "/consulta?data=13/02/2018")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(Boolean.FALSE)));
    }

    @Test
    public void deveConsultarEncontrarFeriadoLocalPelaData() throws Exception {
        mvc.perform(get(URL_BASE + "/consulta/5578?data=10/12/2018")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(Boolean.TRUE)));
    }

    @Test
    public void deveConsultarNaoEncontrarFeriadoLocalPelaData() throws Exception {
        mvc.perform(get(URL_BASE + "/consulta/?data=10/12/2018")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(Boolean.FALSE)));
    }

    @Test
    public void deveRetornarTodosFeriadosAnoAtual() throws Exception {
        when(dataHoraAtual.getData()).thenReturn(LocalDate.of(2018, 06, 06));
        mvc.perform(get(URL_BASE)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    public void consultarFeriadoComCidadeUf_deveLancarExcecao_seUltimoParametroContiverPonto() {
        assertThatExceptionOfType(NestedServletException.class)
            .isThrownBy(() ->
                mvc.perform(get(URL_BASE + "/cidade/Arapongas/P.R")
                    .header("Authorization", getAccessToken(mvc, ADMIN))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
            )
            .withMessage("Handler dispatch failed; "
                + "nested exception is java.lang.NoClassDefFoundError: com.sun.activation.registries.LogSupport");
    }

    @Test
    public void consultarFeriadoComCidadeUf_deveRetornarOk_seParametrosValidos() throws Exception {
        doReturn(true).when(service).isFeriadoHojeNaCidadeUf(anyString(), anyString());
        mvc.perform(get(URL_BASE + "/cidade/Arapongas/PR")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void cacheClearFeriados_deveChamarMetodo_seUsuarioAutenticado() throws Exception {
        doNothing().when(service).flushCacheFeriados();

        mvc.perform(delete(URL_BASE + "/cache/clear")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        verify(service, times(1)).flushCacheFeriados();
    }

    @Test
    public void cacheClearFeriados_naoDeveChamarMetodo_seNaoEstiverAutenticado() throws Exception {
        mvc.perform(delete(URL_BASE + "/cache/clear")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andReturn();

        verify(service, never()).flushCacheFeriados();
    }

    @Test
    public void buscarTotalDeFeriadosPorMesAno_deveRetornarTotalFeriadosAgrupadoPorAnoMes_quandoSolicitado()
        throws Exception {
        mvc.perform(get(URL_BASE + "/mes-ano/total-feriados")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].ano", is(2018)))
            .andExpect(jsonPath("$[0].mes", is(1)))
            .andExpect(jsonPath("$[0].qtdFeriadosNacionais", is(1)))
            .andExpect(jsonPath("$[1].ano", is(2018)))
            .andExpect(jsonPath("$[1].mes", is(3)))
            .andExpect(jsonPath("$[1].qtdFeriadosNacionais", is(1)))
            .andExpect(jsonPath("$[2].ano", is(2018)))
            .andExpect(jsonPath("$[2].mes", is(4)))
            .andExpect(jsonPath("$[2].qtdFeriadosNacionais", is(1)))
            .andExpect(jsonPath("$[3].ano", is(2018)))
            .andExpect(jsonPath("$[3].mes", is(5)))
            .andExpect(jsonPath("$[3].qtdFeriadosNacionais", is(1)))
            .andExpect(jsonPath("$[4].ano", is(2018)))
            .andExpect(jsonPath("$[4].mes", is(9)))
            .andExpect(jsonPath("$[4].qtdFeriadosNacionais", is(1)))
            .andExpect(jsonPath("$[5].ano", is(2018)))
            .andExpect(jsonPath("$[5].mes", is(10)))
            .andExpect(jsonPath("$[5].qtdFeriadosNacionais", is(1)))
            .andExpect(jsonPath("$[6].ano", is(2018)))
            .andExpect(jsonPath("$[6].mes", is(11)))
            .andExpect(jsonPath("$[6].qtdFeriadosNacionais", is(2)))
            .andExpect(jsonPath("$[7].ano", is(2018)))
            .andExpect(jsonPath("$[7].mes", is(12)))
            .andExpect(jsonPath("$[7].qtdFeriadosNacionais", is(1)));
    }
}
