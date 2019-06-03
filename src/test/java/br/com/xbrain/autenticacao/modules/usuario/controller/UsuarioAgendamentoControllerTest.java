package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.mailing.service.TabulacaoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.ColaboradorVendasService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioAgendamentoService;
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

import static br.com.xbrain.autenticacao.modules.usuario.helpers.AgendamentoHelpers.*;
import static helpers.TestsHelper.convertObjectToJsonString;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.HELP_DESK;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
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
public class UsuarioAgendamentoControllerTest {
    private static final String URL_USUARIOS_AGENDAMENTOS = "/api/usuarios/agendamentos/";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private AgenteAutorizadoService agenteAutorizadoService;
    @MockBean
    private ColaboradorVendasService colaboradorVendasService;
    @MockBean
    private TabulacaoService tabulacaoService;
    @MockBean
    private UsuarioAgendamentoService usuarioAgendamentoService;

    @Before
    public void setup() {
        when(usuarioAgendamentoService.getAgendamentoDistribuicaoDoUsuario(eq(140)))
                .thenReturn(agendamentoDistribuicaoListagemResponse());
    }

    @Test
    public void getAgendamentoDistribuicaoDoUsuario_deveRetornar403_seNaoPossuirPermissao() throws Exception {
        mvc.perform(get(URL_USUARIOS_AGENDAMENTOS + "140")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, HELP_DESK)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getAgendamentoDistribuicaoDoUsuario_deveRetornar200_seUsuarioPossuirPermissao() throws Exception {
        mvc.perform(get(URL_USUARIOS_AGENDAMENTOS + "140")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].agenteAutorizadoId", is(1400)))
                .andExpect(jsonPath("$[0].quantidadeAgendamentos", is(0)))
                .andExpect(jsonPath("$[1].agenteAutorizadoId", is(1401)))
                .andExpect(jsonPath("$[1].quantidadeAgendamentos", is(1)));
    }

    @Test
    public void distribuirAgendamentosDoUsuario_deveRetornar403_seNaoPossuirPermissao() throws Exception {
        mvc.perform(post(URL_USUARIOS_AGENDAMENTOS)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, HELP_DESK))
                .content(convertObjectToJsonString(umAgendamentoDistribuicaoRequestDoUsuario140())))
                .andExpect(status().isForbidden());
    }

    @Test
    public void distribuirAgendamentosDoUsuario_deveRetornar200_sePossuirPermissao() throws Exception {
        mvc.perform(post(URL_USUARIOS_AGENDAMENTOS)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .content(convertObjectToJsonString(umAgendamentoDistribuicaoRequestDoUsuario141())))
                .andExpect(status().isOk());
    }
}