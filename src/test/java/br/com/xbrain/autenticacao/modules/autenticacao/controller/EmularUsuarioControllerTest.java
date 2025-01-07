package br.com.xbrain.autenticacao.modules.autenticacao.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.HELP_DESK;
import static org.hamcrest.Matchers.*;
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
public class EmularUsuarioControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DataHoraAtual dataHoraAtual;

    @Test
    public void deveSolicitarAutenticacao() throws Exception {
        mvc.perform(get("/api/emular")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deveTerPermissaoDeGerenciaDeUsuario() throws Exception {
        mvc.perform(get("/api/emular")
                .header("Authorization", getAccessToken(mvc, HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deveEmularOUsuario() throws Exception {
        mvc.perform(get("/api/emular/usuario?id=101")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", notNullValue()))
                .andExpect(jsonPath("$.login", is("101-HELPDESK@XBRAIN.COM.BR")))
                .andExpect(jsonPath("$.nome", is("HELPDESK")))
                .andExpect(jsonPath("$.alterarSenha", is("F")))
                .andExpect(jsonPath("$.nivel", is("X-BRAIN")))
                .andExpect(jsonPath("$.departamento", is("HelpDesk")))
                .andExpect(jsonPath("$.cargo", is("Analista de Suporte")));
    }

    @Test
    @WithMockUser
    public void deveNaoPermitirEmularUmUsuarioAPartirDeOutraEmulacao() throws Exception {
        mvc.perform(get("/api/emular/usuario?id=102")
                .header(AutenticacaoService.HEADER_USUARIO_EMULADOR, "101")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                        "Já existe uma emulação em execução! Encerre a atual para iniciar uma outra."
                )));
    }

    @Test
    @WithMockUser
    public void deveNaoPermitirEmularUmUsuarioQuandoEstiverForaDoHorarioPermitido() throws Exception {
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(2022, 03, 02, 14, 30));

        mvc.perform(get("/api/emular/usuario?id=402")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                        "Usuário fora do horário permitido."
                )));
    }
}
