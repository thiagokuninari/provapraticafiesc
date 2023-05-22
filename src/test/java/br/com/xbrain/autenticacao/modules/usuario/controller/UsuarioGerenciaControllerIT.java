package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.HELP_DESK;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.thymeleaf.util.StringUtils.concat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("classpath:/tests_database.sql")
public class UsuarioGerenciaControllerIT {

    private static final int ID_USUARIO_HELPDESK = 101;
    private static final String API_URI = "/api/usuarios/gerencia";
    @Autowired
    private MockMvc mvc;
    @SpyBean
    private UsuarioService usuarioService;

    @Test
    @SneakyThrows
    public void getById_deveRetornarOUsuario_quandoInformadoOId() {
        mvc.perform(get(concat(API_URI, "/", ID_USUARIO_HELPDESK))
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(ID_USUARIO_HELPDESK)))
            .andExpect(jsonPath("$.nome", is("HELPDESK")))
            .andExpect(jsonPath("$.nivelId", notNullValue()));

        verify(usuarioService).findByIdComAa(101);
    }

    @Test
    @SneakyThrows
    public void getById_deveRetornarUnauthorized_seUsuarioNaoAutenticao() {
        mvc.perform(get(concat(API_URI, "/", ID_USUARIO_HELPDESK))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    public void getById_deveRetornarForbidden_seUsuarioSemPermissao() {
        mvc.perform(get(concat(API_URI, "/", ID_USUARIO_HELPDESK))
                .header("Authorization", getAccessToken(mvc, HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }
}
