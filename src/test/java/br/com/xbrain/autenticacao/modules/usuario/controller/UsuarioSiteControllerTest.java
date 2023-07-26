package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioSiteService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static helpers.Usuarios.ADMIN;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UsuarioSiteController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(UsuarioSubCanalObserver.class),
    @MockBean(TokenStore.class),
})
@Import(OAuth2ResourceConfig.class)
public class UsuarioSiteControllerTest {
    private static final String USUARIOS_SITE_ENDPOINT = "/api/usuarios/site";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UsuarioSiteService usuarioSiteService;

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void buscarUsuariosDisponiveisPorCargo_deveBuscarUsuariosPorSite_quandoSolicitado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/coordenadores/disponiveis")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioSiteService).buscarCoordenadoresDisponiveis();
    }

    @Test
    @SneakyThrows
    public void buscarUsuariosDisponiveisPorCargo_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/coordenadores/disponiveis")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioSiteService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void editarCoordenadorSite_deveEditarCoordenadorSite_seUsuarioAutenticado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/editar/1/coordenador")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioSiteService).buscarCoordenadoresDisponiveisEVinculadosAoSite(1);
    }

    @Test
    @SneakyThrows
    public void editarCoordenadorSite_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/editar/1/coordenador")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioSiteService);
    }

    @Test
    @SneakyThrows
    public void getUsuariosIdsDaHierarquiaDoUsuarioLogado_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/hierarquia-usuario-logado"))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioSiteService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getUsuariosIdsDaHierarquiaAtivoLocalDoUsuarioLogado_deveRetornarIds_seUsuarioAutenticado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/hierarquia-usuario-logado"))
            .andExpect(status().isOk());

        verify(usuarioSiteService).getUsuariosDaHierarquiaDoUsuarioLogado();
    }

    @Test
    @SneakyThrows
    public void getVendoresSelectDoSiteIdPorHierarquiaDoUsuarioLogado_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/100/vendedores-hierarquia-usuario-logado")
                .param("buscarInativo", "false"))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioSiteService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getVendoresSelectDoSiteIdPorHierarquiaDoUsuarioLogado_deveBuscarVendedores_seUsuarioAutenticado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/100/vendedores-hierarquia-usuario-logado")
                .param("buscarInativo", "true"))
            .andExpect(status().isOk());

        verify(usuarioSiteService)
            .getVendoresSelectDoSiteIdPorHierarquiaDoUsuarioLogado(eq(100), eq(true));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getVendedoresOperacaoAtivoProprio_deveBuscarVendedores_quandoSolicitado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/1/vendedores")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioSiteService).getVendedoresOperacaoAtivoProprioPorSiteId(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getVendedoresOperacaoAtivoProprio_deveRetornarUnauthorized_seUsuarioNaoAutorizado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/1/vendedores")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioSiteService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void buscarUsuariosSitePorCargo_deveBuscarListaDeUsuarios_quandoSolicitado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/cargo/AGENTE_AUTORIZADO_SOCIO")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioSiteService).buscarUsuariosSitePorCargo(CodigoCargo.AGENTE_AUTORIZADO_SOCIO);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarUsuariosSitePorCargo_deveRetornarUnauthorized_seUsuarioNaoAutorizado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/cargo/AGENTE_AUTORIZADO_SOCIO")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioSiteService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getSupervidoresSemSitePorCoodenadoresId_deveBuscarListaDeUsuarios_quandoSolicitado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/supervisores-hierarquia/disponiveis")
                .param("coordenadoresIds", "1,2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioSiteService).getSupervisoresSemSitePorCoordenadorsId(List.of(1, 2));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getSupervidoresSemSitePorCoodenadoresId_deveRetornarBadRequest_seCoordenadoresIdsEstiverNulo() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/supervisores-hierarquia/disponiveis")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(usuarioSiteService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getSupervidoresSemSitePorCoodenadoresId_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/supervisores-hierarquia/disponiveis")
                .param("coordenadoresIds", "1,2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioSiteService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getVendoresDoSiteIdPorHierarquiaComEquipe_deveBuscarVendedoresPorSite_seUsuarioAutenticado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/100/vendedores-hierarquia")
                .param("usuarioId", "1")
                .param("buscarInativo", "true")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioSiteService)
            .getVendoresDoSiteIdPorHierarquiaComEquipe(100, 1, true);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getVendoresDoSiteIdPorHierarquiaComEquipe_deveRetornarBadRequest_seUsuarioIdForNulo() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/100/vendedores-hierarquia")
                .param("buscarInativo", "true"))
            .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(usuarioSiteService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getVendoresDoSiteIdPorHierarquiaComEquipe_deveRetornarUnauthorized_seUsuarioNaoAutorizado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/100/vendedores-hierarquia")
                .param("usuarioId", "1")
                .param("buscarInativo", "true"))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioSiteService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void coordenadoresDoSiteId_deveRetornarCoordenadoresPorSiteId_seUsuarioAutenticado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/1/coordenadores")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioSiteService).coordenadoresDoSiteId(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void coordenadoresDoSiteId_deveRetornarUnauthorized_seUsuarioNaoAutorizado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/1/coordenadores")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioSiteService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getUsuariosIdsDaHierarquiaDoUsuarioLogado_deveRetornarIdsDaDaHierarquia_seUsuarioAutenticado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/hierarquia-usuario-logado")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioSiteService).getUsuariosDaHierarquiaDoUsuarioLogado();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosIdsDaHierarquiaDoUsuarioLogado_deveRetornarUnauthorized_seUsuarioNaoAutorizado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/hierarquia-usuario-logado")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioSiteService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void buscarCoordenadorSite_deveRetornarCoordenadoresVinculadosAoSite_seUsuarioAutenticado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/editar/1/coordenador")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioSiteService).buscarCoordenadoresDisponiveisEVinculadosAoSite(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarCoordenadorSite_deveRetornarUnauthorized_seUsuarioNaoAutorizado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/editar/1/coordenador")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioSiteService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void buscarSupervisorSite_deveRetornarSupervisoresVinculadosAoSite_seUsuarioAutenticado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/editar/1/supervisor")
                .param("coordenadoresIds", "1, 2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioSiteService).buscarSupervisoresDisponiveisEVinculadosAoSite(List.of(1, 2), 1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void buscarSupervisorSite_deveRetornarBadRequest_seParametroCoordenadoresIdsNaoImformado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/editar/1/supervisor")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(usuarioSiteService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarSupervisorSite_deveRetornarUnauthorized_seUsuarioNaoAutorizado() {
        mvc.perform(get(USUARIOS_SITE_ENDPOINT + "/editar/1/supervisor")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioSiteService);
    }
}
