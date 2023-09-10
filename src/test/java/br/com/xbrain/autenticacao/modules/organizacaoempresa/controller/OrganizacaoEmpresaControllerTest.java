package br.com.xbrain.autenticacao.modules.organizacaoempresa.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaFiltros;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaRequest;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.service.OrganizacaoEmpresaService;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.OrganizacaoEmpresaHelper.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import(OAuth2ResourceConfig.class)
@WebMvcTest(controllers = OrganizacaoEmpresaController.class)
public class OrganizacaoEmpresaControllerTest {

    private static final String API_URI = "/api/organizacao-empresa";
    private static final String GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO = "VAR_GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TokenStore tokenStore;
    @MockBean
    private OrganizacaoEmpresaService service;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @MockBean
    private UsuarioSubCanalObserver usuarioSubCanalObserver;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getOrganizacaoEmpresa_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mockMvc.perform(get(API_URI))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getAll(any(), any());
    }

    @Test
    @SneakyThrows
    public void getOrganizacaoEmpresa_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mockMvc.perform(get(API_URI))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).getAll(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void getOrganizacaoEmpresa_deveRetornarOk_quandoDadosVaidos() {
        when(service.getAll(new OrganizacaoEmpresaFiltros(), new PageRequest())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get(API_URI))
            .andExpect(status().isOk());

        verify(service).getAll(new OrganizacaoEmpresaFiltros(), new PageRequest());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findById_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mockMvc.perform(get(API_URI + "/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).findById(any());
    }

    @Test
    @SneakyThrows
    public void findById_deveRetornarForbidden_quandoUsuarioSemPermissao() {
        mockMvc.perform(get(API_URI + "/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).findById(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void findById_deveRetornarOk_quandoDadosValidos() {
        mockMvc.perform(get(API_URI + "/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).findById(eq(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void save_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mockMvc.perform(post(API_URI))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).save(any());
    }

    @Test
    @SneakyThrows
    public void save_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mockMvc.perform(post(API_URI)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).save(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void save_deveRetornarBadRequest_quandoDadoObrigatorioNull() {
        mockMvc.perform(post(API_URI)
                .content(convertObjectToJsonBytes(new OrganizacaoEmpresaRequest()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo nivelId é obrigatório.",
                "O campo modalidadesEmpresaIds é obrigatório.",
                "O campo razaoSocial é obrigatório.",
                "O campo cnpj may not be empty")));

        verify(service, never()).save(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void save_deveRetornarBadRequest_quandoDadoObrigatorioListEmpty() {
        var requestEmpty = organizacaoEmpresaRequest();
        requestEmpty.setModalidadesEmpresaIds(List.of());

        mockMvc.perform(post(API_URI)
                .content(convertObjectToJsonBytes(requestEmpty))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo modalidadesEmpresaIds é obrigatório.")));

        verify(service, never()).save(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void save_deveRetornarBadRequest_quandoDadoObrigatorioStringBlank() {
        var requestBlank = organizacaoEmpresaRequest();
        requestBlank.setRazaoSocial("   ");
        requestBlank.setCnpj("   ");

        mockMvc.perform(post(API_URI)
                .content(convertObjectToJsonBytes(requestBlank))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo cnpj não é um cnpj válido.",
                "O campo cnpj may not be empty")));

        verify(service, never()).save(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void save_deveRetornarCreated_quandoDadosValidos() {
        when(service.save(organizacaoEmpresaRequest())).thenReturn(organizacaoEmpresa());

        mockMvc.perform(post(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(organizacaoEmpresaRequest())))
            .andExpect(status().isCreated());

        verify(service).save(eq(organizacaoEmpresaRequest()));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void inativar_deveRetornarUnauthorized_quandoNaoAutenticado() {
        mockMvc.perform(put(API_URI + "/{id}/inativar", 2))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).inativar(any());
    }

    @Test
    @SneakyThrows
    public void inativar_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mockMvc.perform(put(API_URI + "/{id}/inativar", 2))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).inativar(any());
    }

    @Test
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    @SneakyThrows
    public void inativar_deveRetornarOk_quandoDadosValidos() {
        mockMvc.perform(put(API_URI + "/{id}/inativar", 2))
            .andExpect(status().isOk());

        verify(service).inativar(eq(2));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void ativar_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mockMvc.perform(put(API_URI + "/{id}/ativar", 2))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).ativar(any());
    }

    @Test
    @SneakyThrows
    public void ativar_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mockMvc.perform(put(API_URI + "/{id}/ativar", 2))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).ativar(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void ativar_deveRetornarOk_quandoDadosValidos() {
        mockMvc.perform(put(API_URI + "/{id}/ativar", 2))
            .andExpect(status().isOk());

        verify(service).ativar(eq(2));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void update_deveRetornarUnauthorized_quandoNaoAutenticado() {
        mockMvc.perform(put(API_URI + "/{id}/editar", 5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(organizacaoEmpresaRequest())))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).update(any(), any());
    }

    @Test
    @SneakyThrows
    public void update_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mockMvc.perform(put(API_URI + "/{id}/editar", 5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(organizacaoEmpresaRequest())))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).update(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void update_deveRetornarBadRequest_quandoDadoObrigatorioNull() {
        when(service.update(5, new OrganizacaoEmpresaRequest())).thenReturn(organizacaoEmpresa());

        mockMvc.perform(put(API_URI + "/{id}/editar", 5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new OrganizacaoEmpresaRequest())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo nivelId é obrigatório.",
                "O campo modalidadesEmpresaIds é obrigatório.",
                "O campo razaoSocial é obrigatório.",
                "O campo cnpj may not be empty")));

        verify(service, never()).update(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void update_deveRetornarBadRequest_quandoDadoObrigatorioListEmpty() {
        var requestEmpty = organizacaoEmpresaRequest();
        requestEmpty.setModalidadesEmpresaIds(List.of());

        mockMvc.perform(put(API_URI + "/{id}/editar", 5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(requestEmpty)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo modalidadesEmpresaIds é obrigatório.")));

        verify(service, never()).update(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void update_deveRetornarBadRequest_quandoDadoObrigatorioStringBlank() {
        var requestBlank = organizacaoEmpresaRequest();
        requestBlank.setRazaoSocial("   ");
        requestBlank.setCnpj("  ");

        mockMvc.perform(put(API_URI + "/{id}/editar", 5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new OrganizacaoEmpresaRequest())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo nivelId é obrigatório.",
                "O campo modalidadesEmpresaIds é obrigatório.",
                "O campo razaoSocial é obrigatório.",
                "O campo cnpj may not be empty")));

        verify(service, never()).update(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void update_deveRetornarOk_quandoDadosValidos() {
        when(service.update(5, organizacaoEmpresaRequest())).thenReturn(organizacaoEmpresa());

        mockMvc.perform(put(API_URI + "/{id}/editar", 5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(organizacaoEmpresaRequest())))
            .andExpect(status().isOk());

        verify(service).update(eq(5), eq(organizacaoEmpresaRequest()));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findAllAtivosByNivelId_deveRetornarUnauthorized_quandoNaoPassarToken() {
        when(service.findAllAtivosByNivelId(eq(100))).thenReturn(umaListaOrganizacaoEmpresaResponse());
        mockMvc.perform(get(API_URI + "/nivel")
                .param("nivelId", "100")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).findAllAtivosByNivelId(any());
    }

    @Test
    @SneakyThrows
    public void findAllByNivelId_deveRetornarForbidden_quandoNaoTiverPermissao() {
        when(service.findAllAtivosByNivelId(eq(100))).thenReturn(umaListaOrganizacaoEmpresaResponse());

        mockMvc.perform(get(API_URI + "/nivel")
                .param("nivelId", "100")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).findAllAtivosByNivelId(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void findAllByNivelId_deveRetornarBadRequest_quandonNivelIdNaoInformado() {
        when(service.findAllAtivosByNivelId(eq(100))).thenReturn(umaListaOrganizacaoEmpresaResponse());

        mockMvc.perform(get(API_URI + "/nivel")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(service, never()).findAllAtivosByNivelId(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void findAllByNivelId_deveRetornarOk_quandoDadosValidos() {
        when(service.findAllAtivosByNivelId(100)).thenReturn(umaListaOrganizacaoEmpresaResponse());

        mockMvc.perform(get(API_URI + "/nivel")
                .param("nivelId", "100")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).findAllAtivosByNivelId(eq(100));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findByNivel_deveRetornarUnauthorized_quandoNaoPassarToken() {
        when(service.findAllAtivosByNivelId(eq(100))).thenReturn(umaListaOrganizacaoEmpresaResponse());

        mockMvc.perform(get(API_URI + "/por-nivel")
                .param("nivelId", "100")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).findAllByNivelId(any());
    }

    @Test
    @SneakyThrows
    public void findByNivel_deveRetornarForbidden_quandoNaoTiverPermissao() {
        when(service.findAllByNivelId(eq(100))).thenReturn(umaListaOrganizacaoEmpresaResponse());

        mockMvc.perform(get(API_URI + "/por-nivel")
                .param("nivelId", "100")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).findAllByNivelId(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void findByNivel_deveRetornarBadRequest_quandoDadosValidos() {
        when(service.findAllByNivelId(eq(100))).thenReturn(umaListaOrganizacaoEmpresaResponse());

        mockMvc.perform(get(API_URI + "/por-nivel")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(service, never()).findAllByNivelId(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO})
    public void findByNivel_deveRetornarOk_quandoDadosValidos() {
        when(service.findAllByNivelId(eq(100))).thenReturn(umaListaOrganizacaoEmpresaResponse());

        mockMvc.perform(get(API_URI + "/por-nivel")
                .param("nivelId", "100")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).findAllByNivelId(eq(100));
    }
}
