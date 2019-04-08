package br.com.xbrain.autenticacao.modules.logrequest.controller;

import br.com.xbrain.autenticacao.modules.logrequest.model.LogRequest;
import br.com.xbrain.autenticacao.modules.logrequest.repository.LogRequestRepository;
import br.com.xbrain.autenticacao.modules.logrequest.service.LogRequestService;
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
import org.springframework.web.client.RestTemplate;

import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.OPERACAO_GERENTE_COMERCIAL;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class LogRequestInterceptorTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private LogRequestRepository repository;
    @Autowired
    private LogRequestService logRequestService;
    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void deveGerarLogDaConsulta() throws Exception {
        repository.deleteAll();

        mvc.perform(get("/api/usuarios/gerencia")
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Thread.sleep(1000);

        LogRequest logRequest = logRequestService
                .findAll()
                .stream()
                .filter(l ->
                        l.getUrl().equals("/api/usuarios/gerencia"))
                .findFirst()
                .get();

        assertNotNull(logRequest.getId());
        assertNotNull(logRequest.getUrl());
        assertNotNull(logRequest.getUsuario().getId());
        assertNotNull(logRequest.getDataCadastro());

        assertEquals("GET", logRequest.getMethod());
        assertEquals("104", logRequest.getUsuario().getId().toString());
        assertNull(logRequest.getUsuarioEmulador());
    }

    @Test
    public void deveNaoGerarLogDaConsultaXbrain() throws Exception {
        repository.deleteAll();

        mvc.perform(get("/api/usuarios/gerencia")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Thread.sleep(1000);

        assertEquals(0, logRequestService
                .findAll()
                .stream()
                .filter(l -> l.getUrl().equals("/api/usuarios/gerencia"))
                .count());
    }

    @Test
    public void deveGerarLogDaConsultaComUsuarioEmulador() throws Exception {
        repository.deleteAll();

        mvc.perform(get("/api/usuarios/gerencia")
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL))
                .header("X-Usuario-Emulador", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Thread.sleep(1000);

        LogRequest logRequest = logRequestService
                .findAll()
                .stream()
                .filter(l -> l.getUrl().equals("/api/usuarios/gerencia"))
                .findFirst()
                .get();

        assertNotNull(logRequest.getId());
        assertNotNull(logRequest.getUrl());
        assertNotNull(logRequest.getUsuario().getId());
        assertNotNull(logRequest.getDataCadastro());
        assertNotNull(logRequest.getUsuarioEmulador());

        assertEquals("10", logRequest.getUsuarioEmulador().toString());
    }

    @Test
    public void deveNaoGerarLogDaConsultaComUsuarioEmuladorXbrain() throws Exception {
        repository.deleteAll();

        mvc.perform(get("/api/usuarios/gerencia")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .header("X-Usuario-Emulador", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Thread.sleep(1000);

        assertEquals(0, logRequestService
                .findAll()
                .stream()
                .filter(l -> l.getUrl().equals("/api/usuarios/gerencia"))
                .count());
    }
}
