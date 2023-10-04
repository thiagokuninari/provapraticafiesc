package br.com.xbrain.autenticacao.modules.organizacaoempresa.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaFiltros;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.OrganizacaoEmpresaHelper;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.service.OrganizacaoEmpresaService;
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

import java.util.List;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.ControllerTestHelper.*;
import static br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.OrganizacaoEmpresaHelper.*;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class OrganizacaoEmpresaControllerTest {

    private static final String API_URI = "/api/organizacoes";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizacaoEmpresaService organizacaoEmpresaService;

    @MockBean
    private AutenticacaoService autenticacaoService;

    @Test
    @SneakyThrows
    public void ativar_deveRetornarUnauthorized_quandoNaoAutenticado() {
        mockMvc.perform(put(API_URI + "/{id}/ativar", 2))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void ativar_deveRetornarForbidden_quandoNaoTiverPermissao() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioVendedorAutenticado());

        mockMvc.perform(put(API_URI + "/{id}/ativar", 2)
                .header("Authorization", getAccessToken(mockMvc, OPERACAO_ASSISTENTE)))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void inativar_deveRetornarUnauthorized_quandoNaoAutenticado() {
        mockMvc.perform(put(API_URI + "/{id}/inativar", 2))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void inativar_deveRetornarForbidden_quandoNaoTiverPermissao() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioMsoConsultorAutenticado());

        mockMvc.perform(put(API_URI + "/{id}/inativar", 2)
                .header("Authorization", getAccessToken(mockMvc, HELP_DESK)))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void inativar_deveRetornarOk_seForPossivelInativarOrganizacao() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAdminAutenticado());

        mockMvc.perform(put(API_URI + "/{id}/inativar", 2)
                .header("Authorization", getAccessToken(mockMvc, ADMIN))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void ativar_deveRetornarOk_seForPossivelAtivarOrganizacao() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAdminAutenticado());

        mockMvc.perform(put(API_URI + "/{id}/ativar", 2)
                .header("Authorization", getAccessToken(mockMvc, ADMIN))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void ativar_deveRetornarBadRequest_quandoPathForInvalido() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAdminAutenticado());

        doThrow(new ValidacaoException("Organização não encontrada.")).when(organizacaoEmpresaService).ativar(100);

        mockMvc.perform(put(API_URI + "/{id}/ativar", 100)
                .header("Authorization", getAccessToken(mockMvc, ADMIN))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void save_deveRetornarOk_seNaoExistirOrganizacaoCadastrada() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAdminAutenticado());

        var salvarOrganizacao = organizacaoEmpresaRequest();

        mockMvc.perform(post(API_URI)
                .header("Authorization", getAccessToken(mockMvc, ADMIN))
                .contentType(APPLICATION_JSON)
                .content(OrganizacaoEmpresaHelper.convertObjectToJsonBytes(salvarOrganizacao)))
            .andExpect(status().isCreated());
    }

    @Test
    @SneakyThrows
    public void save_deveRetornarForbidden_seNaoTiverPermissao() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioMsoConsultorAutenticado());

        var salvarOrganizacao = organizacaoEmpresaRequest();

        mockMvc.perform(post(API_URI)
                .header("Authorization", getAccessToken(mockMvc, HELP_DESK))
                .contentType(APPLICATION_JSON)
                .content(OrganizacaoEmpresaHelper.convertObjectToJsonBytes(salvarOrganizacao)))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void update_deveRetornarUnauthorized_quandoNaoAutenticado() {
        mockMvc.perform(put(API_URI + "/{id}", 5)
                .contentType(APPLICATION_JSON)
                .content(convertObjectToJsonBytes(organizacaoEmpresaRequest())))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void update_deveRetornarForbidden_seNaoTiverPermissao() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioMsoConsultorAutenticado());

        mockMvc.perform(put(API_URI + "/{id}", 5)
                .header("Authorization", getAccessToken(mockMvc, HELP_DESK))
                .contentType(APPLICATION_JSON)
                .content(convertObjectToJsonBytes(organizacaoEmpresaRequest())))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void getOrganizacaoEmpresa_deveRetornarUnauthorized_quandoNaoAutenticado() {
        mockMvc.perform(get(API_URI))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void getOrganizacaoEmpresa_deveRetornarListaDeOrganizacaoEmpresa_quandoExistiremOrganizacoesCadastradas() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAdminAutenticado());

        when(organizacaoEmpresaService.getAll(any(), any()))
            .thenReturn(organizacaoEmpresaPage());

        mockMvc.perform(get(API_URI)
                .header("Authorization", getAccessToken(mockMvc, ADMIN))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id", is(1)))
            .andExpect(jsonPath("$.content[0].nome", is("Teste AA")))
            .andExpect(jsonPath("$.content[0].situacao", is("A")))
            .andExpect(jsonPath("$.content[0].codigo", is("codigo")))
            .andExpect(jsonPath("$.content[1].id", is(2)))
            .andExpect(jsonPath("$.content[1].nome", is("Teste AA Dois")))
            .andExpect(jsonPath("$.content[1].situacao", is("A")))
            .andExpect(jsonPath("$.content[1].codigo", is("codigo2")));
    }

    @Test
    @SneakyThrows
    public void findById_deveRetornarOrganizacaoEmpresaPorId_quandoExistirOrganizacoesCadastradas() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAdminAutenticado());

        when(organizacaoEmpresaService.findById(any()))
            .thenReturn(umaOrganizacaoEmpresa());

        mockMvc.perform(get(API_URI + "/{id}", 1)
                .header("Authorization", getAccessToken(mockMvc, ADMIN))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.nome", is("Teste AA")))
            .andExpect(jsonPath("$.situacao", is("A")))
            .andExpect(jsonPath("$.codigo", is("codigo")));

    }

    @Test
    @SneakyThrows
    public void findById_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mockMvc.perform(get(API_URI + "/{id}", 1)
                .contentType(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void findAllAtivos_deveRetornarListaOrganizacoesEmpresaAtivaIdsPorNivelId_quandoSolicitado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAdminAutenticado());

        when(organizacaoEmpresaService.findAllAtivos(any())).thenReturn(umaListaOrganizacaoEmpresaResponse());

        mockMvc.perform(get(API_URI + "/consultar-ativos")
                .header("Authorization", getAccessToken(mockMvc, ADMIN))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].situacao", is("A")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].situacao", is("A")));
    }

    @Test
    @SneakyThrows
    public void findAllByNivelId_deveRetornarListaOrganizacoesEmpresaIdsPorNivelId_quandoSolicitado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAdminAutenticado());

        when(organizacaoEmpresaService.findAllByNivelId(eq(100))).thenReturn(umaListaOrganizacaoEmpresaResponse());
        mockMvc.perform(get(API_URI + "/por-nivel")
                .header("Authorization", getAccessToken(mockMvc, ADMIN))
                .param("nivelId", "100")
                .accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    public void getAllSelect_todasOrganizacoes_quandoSolicitar() throws Exception {
        when(organizacaoEmpresaService.getAllSelect(new OrganizacaoEmpresaFiltros()))
            .thenReturn(List.of(SelectResponse.builder().value(1).label("Teste AA").build()));

        mockMvc.perform(get(API_URI + "/select")
                .header("Authorization", getAccessToken(mockMvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].value", is(1)))
            .andExpect(jsonPath("$[0].label", is("Teste AA")));
    }

    @Test
    @SneakyThrows
    public void getAllSelect_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mockMvc.perform(get(API_URI + "/select")
                .contentType(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void getAllSelect_deveRetornarBadRequest_quandoParamentrosForemInvalidos() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAdminAutenticado());
        when(organizacaoEmpresaService.getAllSelect(new OrganizacaoEmpresaFiltros()))
            .thenReturn(List.of(SelectResponse.builder().value(2).label("Teste AA").build()));

        mockMvc.perform(get(API_URI + "/select?organizacaoId=e&nome=2")
                .header("Authorization", getAccessToken(mockMvc, ADMIN))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void findAllOrganizacoesAtivasByNiveisIds_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        when(organizacaoEmpresaService.findAllOrganizacoesAtivasByNiveisIds(eq(List.of(1,2))))
            .thenReturn(umaListaOrganizacaoEmpresaResponseComNivel());

        mockMvc.perform(get(API_URI + "/niveis-ids")
                .param("niveisIds", "1,2")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void findAllOrganizacoesAtivasByNiveisIds_deveRetornarBadRequest_quandoParametroNaoPreenchidoCorretamente() {
        mockMvc.perform(get(API_URI + "/niveis-ids")
                .param("niveisIds", "a")
                .header("Authorization", getAccessToken(mockMvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void findAllOrganizacoesAtivasByNiveisIds_deveRetornarListaOrganizacoesEmpresaIdsPorNiveisId_quandoSolicitado() {
        when(organizacaoEmpresaService.findAllOrganizacoesAtivasByNiveisIds(eq(List.of(1,2))))
            .thenReturn(umaListaOrganizacaoEmpresaResponseComNivel());

        mockMvc.perform(get(API_URI + "/niveis-ids")
                .param("niveisIds", "1,2")
                .header("Authorization", getAccessToken(mockMvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    @SneakyThrows
    public void verificarOrganizacaoAtiva_deveRetornarSituacaoOrganizacao_quandoSolicitado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAdminAutenticado());

        when(organizacaoEmpresaService.isOrganizacaoAtiva("ORGANIZACAO"))
            .thenReturn(true);

        mockMvc.perform(get(API_URI + "/{organizacao}/ativa", "ORGANIZAO")
                .header("Authorization", getAccessToken(mockMvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void isOrganizacaoAtiva_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        when(organizacaoEmpresaService.isOrganizacaoAtiva("ORGANIZACAO"))
            .thenReturn(true);

        mockMvc.perform(get(API_URI + "/{organizacao}/ativa", "ORGANIZACAO")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

}
