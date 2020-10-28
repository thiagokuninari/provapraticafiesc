package br.com.xbrain.autenticacao.modules.cep.controller;

import br.com.xbrain.autenticacao.modules.cep.client.ConsultaCepClient;
import br.com.xbrain.autenticacao.modules.cep.dto.ConsultaCepResponse;
import br.com.xbrain.autenticacao.modules.cep.service.ConsultaCepService;
import helpers.Usuarios;
import org.junit.Before;
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

import static helpers.TestsHelper.getAccessToken;
import static org.hamcrest.Matchers.is;
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
public class CepControllerTest {

    @MockBean
    private ConsultaCepClient consultaCepClient;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ConsultaCepService consultaCepService;

    @Before
    public void setUp() {
        when(consultaCepClient.consultarCep("86080260"))
            .thenReturn(umaConsultaCep());
    }

    @Test
    public void buscarCidadeEstado_deveRetornarCidadeEstadoComStatus200_quandoExistir() throws Exception {
        mvc.perform(get("/api/cep/86080-260")
            .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cidadeId", is(5578)))
            .andExpect(jsonPath("$.cidade", is("LONDRINA")))
            .andExpect(jsonPath("$.uf", is("PARANA")))
            .andExpect(jsonPath("$.ufId", is(1)));

    }

    private ConsultaCepResponse umaConsultaCep() {
        ConsultaCepResponse consulta = new ConsultaCepResponse();
        consulta.setCidade("LONDRINA");
        consulta.setUf("PR");
        return consulta;
    }
}
