package br.com.xbrain.autenticacao.modules.usuario.controller;

import helpers.Usuarios;
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

import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.OPERACAO_GERENTE_COMERCIAL;
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
public class NivelControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void deveSolicitarAutenticacao() throws Exception  {
        mvc.perform(get("/api/niveis")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deveRetornarOsNiveis() throws Exception  {
        mvc.perform(get("/api/niveis")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].nome", is("Agente Autorizado")));
    }

    @Test
    public void deveRetornarOsNiveisAtivosCadastro() throws Exception  {
        mvc.perform(get("/api/niveis/permitidos-cadastro-usuario")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(8)))
                .andExpect(jsonPath("$[0].nome", is("Atendimento Pessoal")));
    }

    @Test
    public void deveRetornarOsNiveisAtivosLista() throws Exception  {
        mvc.perform(get("/api/niveis/permitidos-lista-usuarios")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(8)))
                .andExpect(jsonPath("$[0].nome", is("Atendimento Pessoal")));
    }

    @Test
    public void deveRetornarOsNiveisSemXbrainCadastro() throws Exception  {
        mvc.perform(get("/api/niveis/permitidos-cadastro-usuario")
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Operação")));
    }

    @Test
    public void deveRetornarOsNiveisSemXbrainLista() throws Exception  {
        mvc.perform(get("/api/niveis/permitidos-lista-usuarios")
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Operação")));
    }

    @Test
    public void deveRetornarTodosComFiltroUsuarioOperacaoGerenteCadastro() throws Exception {
        mvc.perform(get("/api/niveis/permitidos-cadastro-usuario")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Operação")));
    }

    @Test
    public void deveRetornarTodosComFiltroUsuarioOperacaoGerenteLista() throws Exception {
        mvc.perform(get("/api/niveis/permitidos-lista-usuarios")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Operação")));
    }
}
