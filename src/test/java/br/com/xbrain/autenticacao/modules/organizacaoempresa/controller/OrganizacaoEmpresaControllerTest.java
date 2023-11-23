package br.com.xbrain.autenticacao.modules.organizacaoempresa.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
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
import static org.hamcrest.Matchers.hasSize;
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

    private static final String ORGANIZACOES_API = "/api/organizacoes";

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
    public void getAllSelect_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mockMvc.perform(get(ORGANIZACOES_API + "/select")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(service, never()).getAllSelect(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllSelect_deveRetornarBadRequest_quandoParamentrosForemInvalidos() {
        mockMvc.perform(get(ORGANIZACOES_API + "/select?organizacaoId=e&nome=2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(service, never()).getAllSelect(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllSelect_deveRetornarOk_seUsuarioAutenticado() {
        mockMvc.perform(get(ORGANIZACOES_API + "/select")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service, times(1)).getAllSelect(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findById_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mockMvc.perform(get(ORGANIZACOES_API + "/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(service, never()).findById(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findById_deveRetornarOk_seUsuarioPossuirPermissao() {
        mockMvc.perform(get(ORGANIZACOES_API + "/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service, times(1)).findById(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void ativar_deveRetornarUnauthorized_quandoNaoAutenticado() {
        mockMvc.perform(put(ORGANIZACOES_API + "/{id}/ativar", 2))
            .andExpect(status().isUnauthorized());

        verify(service, never()).ativar(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "usuario-sem-permissao", roles = "CRN_GERENCIAR_CHAMADO")
    public void ativar_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mockMvc.perform(put(ORGANIZACOES_API + "/{id}/ativar", 2))
            .andExpect(status().isForbidden());

        verify(service, never()).ativar(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void inativar_deveRetornarUnauthorized_quandoNaoAutenticado() {
        mockMvc.perform(put(ORGANIZACOES_API + "/{id}/inativar", 2))
            .andExpect(status().isUnauthorized());

        verify(service, never()).ativar(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "usuario-sem-permissao", roles = "CRN_GERENCIAR_CHAMADO")
    public void inativar_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mockMvc.perform(put(ORGANIZACOES_API + "/{id}/inativar", 2))
            .andExpect(status().isForbidden());

        verify(service, never()).inativar(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "usuario-admin", roles = "VAR_GERENCIAR_ORGANIZACOES")
    public void inativar_deveRetornarOk_seForPossivelInativarOrganizacao() {
        mockMvc.perform(put(ORGANIZACOES_API + "/{id}/inativar", 2)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service, times(1)).inativar(2);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "usuario-admin", roles = "VAR_GERENCIAR_ORGANIZACOES")
    public void ativar_deveRetornarOk_seForPossivelAtivarOrganizacao() {
        mockMvc.perform(put(ORGANIZACOES_API + "/{id}/ativar", 2)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service, times(1)).ativar(2);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "usuario-admin", roles = "VAR_GERENCIAR_ORGANIZACOES")
    public void save_deveRetornarOk_seUsuarioPossuirPermissao() {
        var salvarOrganizacao = organizacaoEmpresaRequest();

        mockMvc.perform(post(ORGANIZACOES_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(salvarOrganizacao)))
            .andExpect(status().isCreated());

        verify(service, times(1)).save(salvarOrganizacao);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "usuario-sem-permissao", roles = "CRN_GERENCIAR_CHAMADO")
    public void save_deveRetornarForbidden_seNaoTiverPermissao() {
        var salvarOrganizacao = organizacaoEmpresaRequest();

        mockMvc.perform(post(ORGANIZACOES_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(salvarOrganizacao)))
            .andExpect(status().isForbidden());

        verify(service, never()).save(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void update_deveRetornarUnauthorized_quandoNaoAutenticado() {
        mockMvc.perform(put(ORGANIZACOES_API + "/{id}/editar", 5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(organizacaoEmpresaRequest())))
            .andExpect(status().isUnauthorized());

        verify(service, never()).update(anyInt(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "usuario-sem-permissao", roles = "CRN_GERENCIAR_CHAMADO")
    public void update_deveRetornarForbidden_seNaoTiverPermissao() {
        mockMvc.perform(put(ORGANIZACOES_API + "/{id}/editar", 5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(organizacaoEmpresaRequest())))
            .andExpect(status().isForbidden());

        verify(service, never()).update(anyInt(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "usuario-admin", roles = "VAR_GERENCIAR_ORGANIZACOES")
    public void update_deveRetornarOk_seUsuarioPossuirPermissao() {
        mockMvc.perform(put(ORGANIZACOES_API + "/{id}/editar", 5)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(organizacaoEmpresaRequest())))
            .andExpect(status().isOk());

        verify(service, times(1)).update(5, organizacaoEmpresaRequest());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getOrganizacaoEmpresa_deveRetornarUnauthorized_quandoNaoAutenticado() {
        mockMvc.perform(get(ORGANIZACOES_API))
            .andExpect(status().isUnauthorized());

        verify(service, never()).getAll(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "usuario-admin", roles = "VAR_GERENCIAR_ORGANIZACOES")
    public void getOrganizacaoEmpresa_deveRetornarListaDeOrganizacaoEmpresa_quandoExistiremOrganizacoesCadastradas() {
        when(service.getAll(any(), any()))
            .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get(ORGANIZACOES_API)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service, times(1)).getAll(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findAllByNivelId_deveRetornarOk_quandoSolicitado() {
        mockMvc.perform(get(ORGANIZACOES_API + "/por-nivel")
                .param("nivelId", "100")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service, times(1)).findAllByNivelId(100);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findAllOrganizacoesAtivasByNiveisIds_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        when(service.findAllOrganizacoesAtivasByNiveisIds(eq(List.of(1, 2))))
            .thenReturn(umaListaOrganizacaoEmpresaResponseComNivel());

        mockMvc.perform(get(ORGANIZACOES_API + "/niveis-ids")
                .param("niveisIds", "1,2")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findAllAtivos_deveRetornarListaOrganizacoesEmpresaAtivaIdsPorNivelId_quandoSolicitado() {
        when(service.findAllAtivos(any())).thenReturn(umaListaOrganizacaoEmpresaResponse());

        mockMvc.perform(get(ORGANIZACOES_API + "/consultar-ativos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].situacao", is("A")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].situacao", is("A")));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "usuario-admin", roles = "VAR_GERENCIAR_ORGANIZACOES")
    public void findAllOrganizacoesAtivasByNiveisIds_deveRetornarBadRequest_quandoParametroNaoPreenchidoCorretamente() {
        mockMvc.perform(get(ORGANIZACOES_API + "/niveis-ids")
                .param("niveisIds", "a")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "usuario-admin", roles = "VAR_GERENCIAR_ORGANIZACOES")
    public void findAllOrganizacoesAtivasByNiveisIds_deveRetornarListaOrganizacoesEmpresaIdsPorNiveisId_quandoSolicitado() {
        when(service.findAllOrganizacoesAtivasByNiveisIds(eq(List.of(1, 2))))
            .thenReturn(umaListaOrganizacaoEmpresaResponseComNivel());

        mockMvc.perform(get(ORGANIZACOES_API + "/niveis-ids")
                .param("niveisIds", "1,2")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "usuario-admin", roles = "VAR_GERENCIAR_ORGANIZACOES")
    public void verificarOrganizacaoAtiva_deveRetornarSituacaoOrganizacao_quandoSolicitado() {
        when(service.isOrganizacaoAtiva("ORGANIZACAO"))
            .thenReturn(true);

        mockMvc.perform(get(ORGANIZACOES_API + "/{organizacao}/ativa", "ORGANIZAO")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void isOrganizacaoAtiva_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        when(service.isOrganizacaoAtiva("ORGANIZACAO"))
            .thenReturn(true);

        mockMvc.perform(get(ORGANIZACOES_API + "/{organizacao}/ativa", "ORGANIZACAO")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
