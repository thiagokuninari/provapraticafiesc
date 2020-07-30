package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.filtros.OrganizacaoFiltros;
import br.com.xbrain.autenticacao.modules.comum.service.OrganizacaoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

import static br.com.xbrain.autenticacao.modules.comum.helper.OrganizacaoHelper.umaOrganizacao;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class OrganizacaoControllerTest {

    private static final String ORGANIZACOES_API = "/api/organizacoes";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private OrganizacaoService organizacaoService;

    @Test
    public void checkToken_exceptionUnauthorized_quandoNaoTemToken() throws Exception {
        mvc.perform(get(ORGANIZACOES_API)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllSelect_todasOrganizacoes_quandoSolicitar() throws Exception {
        Mockito.when(organizacaoService.getAllSelect(new OrganizacaoFiltros()))
                .thenReturn(List.of(
                        umaOrganizacao(1, "BCC"),
                        umaOrganizacao(2, "CALLINK")));

        mvc.perform(get(ORGANIZACOES_API + "/select")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].value", is(1)))
                .andExpect(jsonPath("$[0].label", is("BCC")))
                .andExpect(jsonPath("$[1].value", is(2)))
                .andExpect(jsonPath("$[1].label", is("CALLINK")));
    }
}
