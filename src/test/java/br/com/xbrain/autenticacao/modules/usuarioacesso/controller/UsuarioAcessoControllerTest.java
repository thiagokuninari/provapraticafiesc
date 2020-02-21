package br.com.xbrain.autenticacao.modules.usuarioacesso.controller;

import br.com.xbrain.autenticacao.modules.usuarioacesso.service.UsuarioAcessoService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    public void inativarUsuariosSemAcesso_deveRetornarHttpStatusOk_quandoUsuarioForAdmin() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(ENDPOINT_USUARIO_ACESSO + "/inativar")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(usuarioAcessoService, times(1)).inativarUsuariosSemAcesso();
    }

    @Test
    public void deletarHistoricoUsuarioAcesso_deveRetornarOk_quandoUsuarioForAdmin() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete(ENDPOINT_USUARIO_ACESSO + "/historico")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(usuarioAcessoService, times(1)).deletarHistoricoUsuarioAcesso();
    }

    @Test
    public void exportRegistrosToCsv_deveGerarCsv_quandoExistir() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(ENDPOINT_USUARIO_ACESSO + "/relatorio")
            .param("dataInicio", "01/01/2020")
            .param("dataFim", "10/01/2020")
            .param("tipo", "LOGIN")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.ALL_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        verify(usuarioAcessoService, times(1)).exportRegistrosToCsv(any(), any());
    }

    @Test
    public void filtrar_deveRetornarRegistrosDeUsuarioAcesso_quandoExistir() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(ENDPOINT_USUARIO_ACESSO)
            .param("dataInicio", "01/01/2020")
            .param("dataFim", "10/01/2020")
            .param("tipo", "LOGIN")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.ALL_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        verify(usuarioAcessoService, times(1)).getAll(any(), any());
    }
}