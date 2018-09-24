package br.com.xbrain.autenticacao.modules.feriado.controller;

import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class FeriadoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void deveConsultarEncontrarFeriadoPelaData() throws Exception {
        mvc.perform(get("/api/feriado/consulta?data=07/09/2018")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(Boolean.TRUE)));
    }

    @Test
    public void deveConsultarNaoEncontrarFeriadoPelaData() throws Exception {
        mvc.perform(get("/api/feriado/consulta?data=13/02/2018")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(Boolean.FALSE)));
    }

    @Test
    public void deveConsultarEncontrarFeriadoLocalPelaData() throws Exception {
        mvc.perform(get("/api/feriado/consulta/5578?data=10/12/2018")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(Boolean.TRUE)));
    }

    @Test
    public void deveConsultarNaoEncontrarFeriadoLocalPelaData() throws Exception {
        mvc.perform(get("/api/feriado/consulta/?data=10/12/2018")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(Boolean.FALSE)));
    }

    @Test
    public void deveSalvarFeriado() throws Exception {
        FeriadoRequest request = new FeriadoRequest();
        request.setNome("Feriado Nacional");
        request.setDataFeriado("05/06/2018");
        mvc.perform(post("/api/feriado/")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is(request.getNome())))
                .andExpect(jsonPath("$.feriadoNacional", is("V")));
    }

    @Test
    public void deveSalvarFeriadoLocal() throws Exception {
        FeriadoRequest request = new FeriadoRequest();
        request.setNome("Feriado Nacional");
        request.setDataFeriado("05/06/2018");
        request.setCidadeId(5578);
        mvc.perform(post("/api/feriado/")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is(request.getNome())))
                .andExpect(jsonPath("$.feriadoNacional", is("F")));
    }
}
