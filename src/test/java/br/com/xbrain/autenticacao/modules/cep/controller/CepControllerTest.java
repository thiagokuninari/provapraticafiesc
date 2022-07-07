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
import static org.mockito.Mockito.doReturn;
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
        doReturn(umaConsultaCep(
            "86080260",
            "Rua Elza Grandi Jorge",
            "Conjunto Professora Hilda Mandarino",
            "LONDRINA",
            "PR"
        ))
            .when(consultaCepClient)
            .consultarCep("86080260");

        doReturn(umaConsultaCep(
            "71930000",
            "Avenida Parque Águas Clarase",
            "Sul (Águas Claras)",
            "BRASILIA",
            "DF"
        ))
            .when(consultaCepClient)
            .consultarCep("71930000");

        doReturn(umaConsultaCep(
            "16400123",
            "Praça Napoleão Laureano",
            "Vila Ramalho",
            "LINS",
            "SP"
        ))
            .when(consultaCepClient)
            .consultarCep("16400123");
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

    @Test
    public void buscarCidadesPorCeps_deveRetornarCidadeEstadoComStatus200_quandoExistir() throws Exception {
        mvc.perform(get("/api/cep")
                .param("ceps", "86080260, 71930000, 16400123")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].cep", is("86080260")))
            .andExpect(jsonPath("$[0].nomeCompleto", is("Rua Elza Grandi Jorge")))
            .andExpect(jsonPath("$[0].bairro", is("Conjunto Professora Hilda Mandarino")))
            .andExpect(jsonPath("$[0].cidade", is("LONDRINA")))
            .andExpect(jsonPath("$[0].uf", is("PR")))
            .andExpect(jsonPath("$[1].cep", is("71930000")))
            .andExpect(jsonPath("$[1].nomeCompleto", is("Avenida Parque Águas Clarase")))
            .andExpect(jsonPath("$[1].bairro", is("Sul (Águas Claras)")))
            .andExpect(jsonPath("$[1].cidade", is("BRASILIA")))
            .andExpect(jsonPath("$[1].uf", is("DF")))
            .andExpect(jsonPath("$[2].cep", is("16400123")))
            .andExpect(jsonPath("$[2].nomeCompleto", is("Praça Napoleão Laureano")))
            .andExpect(jsonPath("$[2].bairro", is("Vila Ramalho")))
            .andExpect(jsonPath("$[2].cidade", is("LINS")))
            .andExpect(jsonPath("$[2].uf", is("SP")));

    }

    private ConsultaCepResponse umaConsultaCep(String cep, String nomeCompleto, String bairro, String cidade, String uf) {
        return ConsultaCepResponse
            .builder()
            .cep(cep)
            .nomeCompleto(nomeCompleto)
            .bairro(bairro)
            .cidade(cidade)
            .uf(uf)
            .build();
    }
}
