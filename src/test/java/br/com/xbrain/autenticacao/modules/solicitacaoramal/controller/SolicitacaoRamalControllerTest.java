package br.com.xbrain.autenticacao.modules.solicitacaoramal.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.EquipeVendasService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.SocioService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalHistoricoService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.helper.SolicitacaoRamalHelper.*;
import static helpers.TestsHelper.convertObjectToJsonBytes;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SolicitacaoRamalController.class)
@Import(OAuth2ResourceConfig.class)
@WithMockUser
public class SolicitacaoRamalControllerTest {

    private static final String URL_API_SOLICITACAO_RAMAL = "/api/solicitacao-ramal";
    private static final String URL_API_SOLICITACAO_RAMAL_GERENCIAL = "/api/solicitacao-ramal/gerencia";
    private static final String GERENCIAR_SOLICITACAO_RAMAL = "CTR_2034";
    @MockBean
    private SolicitacaoRamalService service;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private SolicitacaoRamalHistoricoService historicoService;
    @MockBean
    private AgenteAutorizadoService agenteAutorizadoService;
    @MockBean
    private AgenteAutorizadoNovoService agenteAutorizadoNovoService;
    @MockBean
    private EquipeVendasService equipeVendasService;
    @MockBean
    private EmailService emailService;
    @MockBean
    private CallService callService;
    @MockBean
    private SocioService socioService;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @MockBean
    private TokenStore tokenStore;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getDadosAgenteAutorizado_deveRetornarAuthorization_quandoNaoPassarToken() {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/dados-agente-autorizado/2"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getDadosAgenteAutorizado(any());
    }

    @Test
    @SneakyThrows
    public void getDadosAgenteAutorizado_deveRetornarOK_quandoDadosValidos() {
        when(agenteAutorizadoNovoService.getUsuariosByAaId(anyInt(), anyBoolean())).thenReturn(umaListaUsuarioResponse());
        when(callService.obterNomeTelefoniaPorId(anyInt())).thenReturn(umaTelefoniaResponse());
        when(callService.obterRamaisParaAgenteAutorizado(anyInt())).thenReturn(umaListaRamalResponse());
        when(socioService.findSocioPrincipalByAaId(anyInt())).thenReturn(umSocioResponse());

        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/dados-agente-autorizado/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(any())))
            .andExpect(status().isOk());

        verify(service).getDadosAgenteAutorizado(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllHistoricoBySolicitacaoId_deveRetornarAuthorization_quandoNaoPassarToken() {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/historico/1"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getAllHistoricoBySolicitacaoId(any());
    }

    @Test
    @SneakyThrows
    public void getAllHistoricoBySolicitacaoId_deveRetornarOK_quandoDadosValidos() {
        when(service.getAllHistoricoBySolicitacaoId(1)).thenReturn(anyList());

        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/historico/1")
                .content(convertObjectToJsonBytes(1)))
            .andExpect(status().isOk());

        verify(service).getAllHistoricoBySolicitacaoId(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getSolicitacaoById_deveRetornarAuthorization_quandoNaoPassarToken() {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/solicitacao/1"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getSolicitacaoById(any());
    }

    @Test
    @SneakyThrows
    public void getSolicitacaoById_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/solicitacao/1")
                .content(convertObjectToJsonBytes(anyInt())))
            .andExpect(status().isOk());

        verify(service).getSolicitacaoById(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void atualizarSituacao_deveRetornarAuthorization_quandoNaoPassarToken() {
        mvc.perform(post(URL_API_SOLICITACAO_RAMAL_GERENCIAL + "/atualiza-status"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).atualizarStatus(any());
    }

    @Test
    @SneakyThrows
    public void atualizarSituacao_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(post(URL_API_SOLICITACAO_RAMAL_GERENCIAL + "/atualiza-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umaSolicitacaoRamalAtualizarRequest())))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).atualizarStatus(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_SOLICITACAO_RAMAL})
    public void atualizarSituacao_deveRetornarBadRequest_quandoDadosObrigatoriosNaoInformado() {
        var solicitacao = umaSolicitacaoRamalAtualizarRequest();
        solicitacao.setIdSolicitacao(null);
        solicitacao.setSituacao(null);

        mvc.perform(post(URL_API_SOLICITACAO_RAMAL_GERENCIAL + "/atualiza-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(solicitacao)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo idSolicitacao é obrigatório.",
                "O campo situacao é obrigatório.")));

        verify(service, never()).atualizarStatus(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_SOLICITACAO_RAMAL})
    public void atualizarSituacao_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(post(URL_API_SOLICITACAO_RAMAL_GERENCIAL + "/atualiza-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umaSolicitacaoRamalAtualizarRequest())))
            .andExpect(status().isOk());

        verify(service).atualizarStatus(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllGerencia_deveRetornarAuthorization_quandoNaoPassarToken() {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL_GERENCIAL))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getAllGerencia(any(), any());
    }

    @Test
    @SneakyThrows
    public void getAllGerencia_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL_GERENCIAL))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).getAllGerencia(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_SOLICITACAO_RAMAL})
    public void getAllGerencia_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL_GERENCIAL))
            .andExpect(status().isOk());

        verify(service).getAllGerencia(any(), any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAll_deveRetornarAuthorization_quandoNaoPassarToken() {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getAll(any(), any());
    }

    @Test
    @SneakyThrows
    public void getAll_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).getAll(any(), any());
    }

    @Test
    @SneakyThrows
    public void getAll_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/?agenteAutorizadoId=1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).getAll(any(), any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void save_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(post(URL_API_SOLICITACAO_RAMAL))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).save(any());
    }

    @Test
    @SneakyThrows
    public void save_deveRetornarForbidden_quandoNaoTiverPermissao() {
        SolicitacaoRamalRequest request = umaSolicitacaoRamalRequest(null, 7129);

        mvc.perform(post(URL_API_SOLICITACAO_RAMAL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(request)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).save(any());
    }

    @Test
    @SneakyThrows
    public void save_deveRetornarBadRequest_quandoDadosObrigatoriosNaoInformado() {
        mvc.perform(post(URL_API_SOLICITACAO_RAMAL + "/?agenteAutorizadoId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umaSolicitacaoRamalRequestNull())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo melhorHorarioImplantacao é obrigatório.",
                "O campo usuariosSolicitadosIds é obrigatório.",
                "O campo melhorDataImplantacao é obrigatório.",
                "O campo agenteAutorizadoId é obrigatório.",
                "O campo tipoImplantacao é obrigatório.",
                "O campo emailTi é obrigatório.",
                "O campo quantidadeRamais é obrigatório.",
                "O campo telefoneTi é obrigatório.")));

        verify(service, never()).save(any());
    }

    @Test
    @SneakyThrows
    public void save_deveRetornarCreated_quandoDadosValidos() {
        SolicitacaoRamalRequest request = umaSolicitacaoRamalRequest(null, 7129);

        mvc.perform(post(URL_API_SOLICITACAO_RAMAL + "/?agenteAutorizadoId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(request)))
            .andExpect(status().isCreated());

        verify(service).save(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void update_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(put(URL_API_SOLICITACAO_RAMAL))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).update(any());
    }

    @Test
    @SneakyThrows
    public void update_deveRetornarForbidden_quandoNaoTiverPermissao() {
        mvc.perform(put(URL_API_SOLICITACAO_RAMAL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umaSolicitacaoRamalRequest(5, 7129))))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).update(any());
    }

    @Test
    @SneakyThrows
    public void update_deveRetornarBadRequest_quandoDadosObrigatoriosNaoInformado() {
        mvc.perform(put(URL_API_SOLICITACAO_RAMAL + "/?agenteAutorizadoId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umaSolicitacaoRamalRequestNull())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo melhorHorarioImplantacao é obrigatório.",
                "O campo usuariosSolicitadosIds é obrigatório.",
                "O campo melhorDataImplantacao é obrigatório.",
                "O campo agenteAutorizadoId é obrigatório.",
                "O campo tipoImplantacao é obrigatório.",
                "O campo emailTi é obrigatório.",
                "O campo quantidadeRamais é obrigatório.",
                "O campo telefoneTi é obrigatório.")));

        verify(service, never()).update(any());
    }

    @Test
    @SneakyThrows
    public void update_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(put(URL_API_SOLICITACAO_RAMAL + "/?agenteAutorizadoId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umaSolicitacaoRamalRequest(5, 7129))))
            .andExpect(status().isOk());

        verify(service).update(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void remover_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(delete(URL_API_SOLICITACAO_RAMAL + "/1"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).remover(any());
    }

    @Test
    @SneakyThrows
    public void remover_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(delete(URL_API_SOLICITACAO_RAMAL + "/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).remover(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getColaboradoresBySolicitacaoId_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/colaboradores/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getColaboradoresBySolicitacaoId(any());
    }

    @Test
    @SneakyThrows
    public void getColaboradoresBySolicitacaoId_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/colaboradores/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).getColaboradoresBySolicitacaoId(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllTipoImplantacao_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/tipo-implantacao"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));
    }

    @Test
    @SneakyThrows
    public void getAllTipoImplantacao_deveRetornarOK_quandoEnumPossuirValores() {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/tipo-implantacao")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[0].codigo", is("ESCRITORIO")))
            .andExpect(jsonPath("$.[0].descricao", is("ESCRITÓRIO")))
            .andExpect(jsonPath("$.[1].codigo", is("HOME_OFFICE")))
            .andExpect(jsonPath("$.[1].descricao", is("HOME OFFICE")));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void calcularDataFinalizacao_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(put(URL_API_SOLICITACAO_RAMAL + "/calcular-data-finalizacao"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).calcularDataFinalizacao(any());
    }

    @Test
    @SneakyThrows
    public void calcularDataFinalizacao_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(put(URL_API_SOLICITACAO_RAMAL + "/calcular-data-finalizacao"))
            .andExpect(status().isOk());

        verify(service).calcularDataFinalizacao(any());
    }
}
