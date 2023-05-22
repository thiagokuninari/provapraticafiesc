package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioBackofficeDto;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
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

import java.time.LocalDate;

import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UsuarioGerenciaController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
})
@Import(OAuth2ResourceConfig.class)
public class UsuarioGerenciaBackofficeControllerTest {

    private static final String API_URI_BACKOFFICE = "/api/usuarios/gerencia/backoffice";
    private static final String API_URI_GERENCIA = "/api/usuarios/gerencia";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private UsuarioService service;

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void save_deveRetornarUsuarioSalvo_quandoNivelForBackoffice() {
        var usuario = umUsuarioDtoBackoffice();

        mvc.perform(post(API_URI_BACKOFFICE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isOk());

        verify(service).salvarUsuarioBackoffice(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void save_badRequest_quandoValidarOsCamposObrigatorios() {
        mvc.perform(post(API_URI_BACKOFFICE)
                .content(convertObjectToJsonBytes(new UsuarioBackofficeDto()))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(6)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo nome é obrigatório.",
                "O campo cpf é obrigatório.",
                "O campo nascimento é obrigatório.",
                "O campo email é obrigatório.",
                "O campo cargoId é obrigatório.",
                "O campo departamentoId é obrigatório.")));

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void save_deveLancarForbidden_seUsuarioNaoTiverPermissao() {
        var usuario = umUsuarioDtoBackoffice();

        mvc.perform(post(API_URI_BACKOFFICE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void save_deveLancarUnauthorized_seUsuarioNaoTiverAutorizacao() {
        var usuario = umUsuarioDtoBackoffice();

        mvc.perform(post(API_URI_BACKOFFICE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getAll_deveRetornarUsuariosBackOffice_quandoUsuarioPossuirPermissao() {
        mvc.perform(get(API_URI_GERENCIA)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).getAll(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void getAll_deveRetornarForbidden_quandoUsuarioSemPermissao() {
        mvc.perform(get(API_URI_GERENCIA)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAll_deveRetornarUnauthorized_quandoSemAutorizacao() {
        mvc.perform(get(API_URI_GERENCIA)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(service);
    }

    private UsuarioBackofficeDto umUsuarioDtoBackoffice() {
        var usuario = new UsuarioBackofficeDto();
        usuario.setNome("USUARIO BACKOFFICE");
        usuario.setCargoId(110);
        usuario.setEmail("usuarioBackoffice@gmail.com");
        usuario.setDepartamentoId(69);
        usuario.setCpf("870.371.018-18");
        usuario.setNascimento(LocalDate.of(2000, 1, 1));
        return usuario;
    }
}
