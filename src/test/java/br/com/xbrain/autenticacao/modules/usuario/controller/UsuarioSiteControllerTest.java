package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioSiteService;
import helpers.TestBuilders;
import lombok.SneakyThrows;
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

import java.util.Collections;
import java.util.List;

import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class UsuarioSiteControllerTest {
    private static final String USUARIOS_SITE_ENDPOINT = "/api/usuarios/site";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UsuarioSiteService usuarioSiteService;

    @Test
    public void buscarUsuariosDisponiveisPorCargo_verificaSeAceitaRequisicaoComMaisDeMilIds() throws Exception {
        mvc.perform(get("/api/usuarios/site/coordenadores/disponiveis")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioSiteService, times(1)).buscarCoordenadoresDisponiveis();
    }

    @Test
    public void editarCoordenadorSite_verificaSeAceitaRequisicaoComMaisDeMilIds() throws Exception {
        mvc.perform(get("/api/usuarios/site/editar/1/coordenador")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioSiteService, times(1)).buscarCoordenadoresDisponiveisEVinculadosAoSite(any());
    }

    @Test
    @SneakyThrows
    public void getUsuariosIdsDaHierarquiaDoUsuarioLogado_unauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/hierarquia-usuario-logado"))
            .andExpect(status().isUnauthorized());

        verify(usuarioSiteService, never()).getUsuariosDaHierarquiaDoUsuarioLogado();
    }

    @Test
    @SneakyThrows
    public void getUsuariosIdsDaHierarquiaAtivoLocalDoUsuarioLogado_deveRetornarIds_seListaNaoVazia() {
        doReturn(List.of(
            TestBuilders.umUsuario(1, CodigoCargo.GERENTE_OPERACAO),
            TestBuilders.umUsuario(2, CodigoCargo.SUPERVISOR_OPERACAO),
            TestBuilders.umUsuario(3, CodigoCargo.COORDENADOR_OPERACAO)))
            .when(usuarioSiteService).getUsuariosDaHierarquiaDoUsuarioLogado();

        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/hierarquia-usuario-logado")
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(List.of(1, 2, 3))));
    }

    @Test
    @SneakyThrows
    public void getUsuariosIdsDaHierarquiaAtivoLocalDoUsuarioLogado_naoDeveRetornarIds_seListaVazia() {
        doReturn(Collections.emptyList())
            .when(usuarioSiteService).getUsuariosDaHierarquiaDoUsuarioLogado();

        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/hierarquia-usuario-logado")
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(Collections.emptyList())));
    }
}
