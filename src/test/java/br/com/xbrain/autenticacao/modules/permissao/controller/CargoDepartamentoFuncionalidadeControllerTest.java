package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadeFiltros;
import br.com.xbrain.autenticacao.modules.permissao.service.CargoDepartamentoFuncionalidadeService;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import helpers.TestsHelper;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static br.com.xbrain.autenticacao.modules.permissao.helper.CargoDepartamentoFuncionalidadeHelper.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(CargoDepartamentoFuncionalidadeController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
    @MockBean(UsuarioSubCanalObserver.class),
})
@Import(OAuth2ResourceConfig.class)
public class CargoDepartamentoFuncionalidadeControllerTest {

    private static final String URL = "/api/cargo-departamento-funcionalidade";
    private static final String USUARIO_SOCIO = "USUARIO SOCIO";
    private static final String USUARIO_ADMIN = "USUARIO ADMIN";
    private static final String AUT_VISUALIZAR_USUARIOS_AA = "AUT_VISUALIZAR_USUARIOS_AA";
    private static final String AUT_GER_PERMISSAO_CARGO_DEPARTAMENTO = "AUT_GER_PERMISSAO_CARGO_DEPARTAMENTO";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private CargoDepartamentoFuncionalidadeService service;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAll_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = USUARIO_SOCIO, roles = { AUT_VISUALIZAR_USUARIOS_AA })
    public void getAll_deveRetornarForbidden_seUsuarioNaoPossuirPermissaoParaGerenciaDePermissao() {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = USUARIO_ADMIN, roles = { AUT_GER_PERMISSAO_CARGO_DEPARTAMENTO })
    public void getAll_deveRetornarOk_seUsuarioPossuirPermissaoParaGerenciaDePermissao() {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllPages_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(MockMvcRequestBuilders.get(URL + "/pages?page=0&size=10")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllPages_deveRetornarOk_seUsuarioAutenticado() {
        when(service.getAll(any(PageRequest.class), any(CargoDepartamentoFuncionalidadeFiltros.class)))
            .thenReturn(new PageImpl<>(umaListaDeCargoDepartamentoFuncionalidadeDeAdministrador()));

        mvc.perform(MockMvcRequestBuilders.get(URL + "/pages?page=0&size=10")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(10)));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void save_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(novasFuncionalidades())))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = USUARIO_SOCIO, roles = { AUT_VISUALIZAR_USUARIOS_AA })
    public void save_deveRetornarForbidden_seUsuarioNaoPossuirPermissaoParaGerenciaDePermissao() {
        mvc.perform(MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(novasFuncionalidades())))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = USUARIO_ADMIN, roles = { AUT_GER_PERMISSAO_CARGO_DEPARTAMENTO })
    public void save_deveRetornarOk_seUsuarioPossuirPermissaoParaGerenciaDePermissao() {
        mvc.perform(MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(novasFuncionalidades())))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void remover_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(put(URL + "/remover/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void remover_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(put(URL + "/remover/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void deslogarUsuarios_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(put(URL + "/deslogar/{cargoId}/{departamentoId}", 50, 50)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void deslogarUsuarios_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(put(URL + "/deslogar/{cargoId}/{departamentoId}", 50, 50)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
