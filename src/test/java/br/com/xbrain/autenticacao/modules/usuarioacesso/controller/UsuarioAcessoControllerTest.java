package br.com.xbrain.autenticacao.modules.usuarioacesso.controller;

import br.com.xbrain.autenticacao.modules.usuarioacesso.service.UsuarioAcessoService;
import helpers.Usuarios;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.OPERACAO_ASSISTENTE;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "classpath:/tests_database.sql")
public class UsuarioAcessoControllerTest {

    private static final String ENDPOINT_USUARIO_ACESSO = "/api/usuario-acesso";
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UsuarioAcessoService usuarioAcessoService;

    @Test
    public void inativarUsuariosSemAcesso_deveRetornarHttpStatusBadRequest_quandoUsuarioNaoForAdmin() throws Exception {
        mvc.perform(get(ENDPOINT_USUARIO_ACESSO + "/inativar")
                .header("Authorization", getAccessToken(mvc, OPERACAO_ASSISTENTE))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(usuarioAcessoService, times(0)).inativarUsuariosSemAcesso();
    }

    @Test
    public void inativarUsuariosSemAcesso_deveRetornarHttpStatusOk_quandoUsuarioForAdmin() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(ENDPOINT_USUARIO_ACESSO + "/inativar")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(usuarioAcessoService, times(1)).inativarUsuariosSemAcesso();
    }

    @Test
    public void deletarRegistros_deveRetornarBadRequest_quandoNaoForUsuarioAdmin() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(ENDPOINT_USUARIO_ACESSO + "/deletar-registros")
                .header("Authorization", getAccessToken(mvc, OPERACAO_ASSISTENTE))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(usuarioAcessoService, times(0)).deletarRegistros();
    }

    @Test
    public void deletarRegistros_deveRetornarOk_quandoUsuarioForAdmin() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(ENDPOINT_USUARIO_ACESSO + "/deletar-registros")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(usuarioAcessoService, times(1)).deletarRegistros();
    }
}