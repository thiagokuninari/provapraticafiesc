package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.dto.ConfiguracaoAgendaRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoConfiguracao;
import br.com.xbrain.autenticacao.modules.usuario.exceptions.SubCanalCustomExceptionHandler;
import br.com.xbrain.autenticacao.modules.usuario.service.ConfiguracaoAgendaRealService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.umaConfiguracaoAgendaRequest;
import static helpers.TestRequisitionHelper.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import(OAuth2ResourceConfig.class)
@MockBeans({
    @MockBean(TokenStore.class),
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(SubCanalCustomExceptionHandler.class)})
@WebMvcTest(controllers = ConfiguracaoAgendaController.class)
public class ConfiguracaoAgendaRealControllerTest {

    private static final String API_URL = "/api/configuracoes/agenda";
    private static final String PERMISSAO_GERENCIA = "AUT_21615";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ConfiguracaoAgendaRealService service;

    @Test
    @WithMockUser(roles = PERMISSAO_GERENCIA)
    public void buscar_deveRetornarOk_quandoPossuirPermissao() {
        isOk(get(API_URL), mvc);
        verify(service).findAll(any(), any());
    }

    @Test
    @WithMockUser
    public void buscar_deveRetornarForbidden_quandoNaoPossuirPermissao() {
        isForbidden(get(API_URL), mvc);
        verifyZeroInteractions(service);
    }

    @Test
    @WithAnonymousUser
    public void buscar_deveRetornarUnauthorized_quandoNaoAutenticado() {
        isUnauthorized(get(API_URL), mvc);
        verifyZeroInteractions(service);
    }

    @Test
    @WithMockUser(roles = PERMISSAO_GERENCIA)
    public void atualizar_deveRetornarOk_quandoPossuirPermissao() {
        isOk(put(API_URL.concat("/2/atualizar"))
            .param("qtdHoras", "100"), mvc);
        verify(service).atualizar(2, 100);
    }

    @Test
    @WithMockUser
    public void atualizar_deveRetornarForbidden_quandoNaoPossuirPermissao() {
        isForbidden(put(API_URL.concat("/2/atualizar")), mvc);
        verifyNoMoreInteractions(service);
    }

    @Test
    @WithAnonymousUser
    public void atualizar_deveRetornarUnauthorized_quandoNaoAutenticado() {
        isUnauthorized(put(API_URL.concat("/2/atualizar")), mvc);
        verifyNoMoreInteractions(service);
    }

    @Test
    @WithMockUser(roles = PERMISSAO_GERENCIA)
    public void ativar_deveRetornarOk_quandoPossuirPermissao() {
        isOk(put(API_URL.concat("/1/ativar")), mvc);
        verify(service).alterarSituacao(1, ESituacao.A);
    }

    @Test
    @WithMockUser
    public void ativar_deveRetornarForbidden_quandoNaoPossuirPermissao() {
        isForbidden(put(API_URL.concat("/1/ativar")), mvc);
        verifyZeroInteractions(service);
    }

    @Test
    @WithAnonymousUser
    public void ativar_deveRetornarUnauthorized_quandoNaoAutenticado() {
        isUnauthorized(put(API_URL.concat("/1/ativar")), mvc);
        verifyZeroInteractions(service);
    }

    @Test
    @WithMockUser(roles = PERMISSAO_GERENCIA)
    public void inativar_deveRetornarOk_quandoPossuirPermissao() {
        isOk(put(API_URL.concat("/1/inativar")), mvc);
        verify(service).alterarSituacao(1, ESituacao.I);
    }

    @Test
    @WithMockUser
    public void inativar_deveRetornarForbidden_quandoNaoPossuirPermissao() {
        isForbidden(put(API_URL.concat("/1/inativar")), mvc);
        verifyZeroInteractions(service);
    }

    @Test
    @WithAnonymousUser
    public void inativar_deveRetornarUnauthorized_quandoNaoAutenticado() {
        isUnauthorized(put(API_URL.concat("/1/inativar")), mvc);
        verifyZeroInteractions(service);
    }

    @Test
    @WithMockUser(roles = PERMISSAO_GERENCIA)
    public void salvar_deveRetornarOk_quandoPossuirPermissaoEPassarDadosObrigatorios() {
        isOk(post(API_URL), mvc, umaConfiguracaoAgendaRequest());
        verify(service).salvar(umaConfiguracaoAgendaRequest());
    }

    @Test
    @WithMockUser(roles = PERMISSAO_GERENCIA)
    public void salvar_deveRetornarBadRequest_quandoPossuirPermissaoENaoPassarDadosObrigatorios() {
        var erroEsperado = jsonPath("$[*].message", containsInAnyOrder(
            "O campo qtdHorasAdicionais é obrigatório.",
            "O campo tipoConfiguracao é obrigatório."
        ));
        isBadRequest(post(API_URL), mvc, new ConfiguracaoAgendaRequest(), erroEsperado);
    }

    @Test
    @WithMockUser(roles = PERMISSAO_GERENCIA)
    public void salvar_deveRetornarBadRequest_quandoNaoPassarParametroDoCanal() {
        var erroEsperado = jsonPath("$[*].message", containsInAnyOrder(
            "O campo canal é obrigatório."
        ));
        isBadRequest(post(API_URL), mvc, umaConfiguracaoAgendaRequest(ETipoConfiguracao.CANAL), erroEsperado);
    }

    @Test
    @WithMockUser(roles = PERMISSAO_GERENCIA)
    public void salvar_deveRetornarBadRequest_quandoNaoPassarParametroDoNivel() {
        var erroEsperado = jsonPath("$[*].message", containsInAnyOrder(
            "O campo nivel é obrigatório."
        ));
        isBadRequest(post(API_URL), mvc, umaConfiguracaoAgendaRequest(ETipoConfiguracao.NIVEL), erroEsperado);
    }

    @Test
    @WithMockUser(roles = PERMISSAO_GERENCIA)
    public void salvar_deveRetornarBadRequest_quandoNaoPassarParametroDaEstrutura() {
        var erroEsperado = jsonPath("$[*].message", containsInAnyOrder(
            "O campo estruturaAa é obrigatório."
        ));
        isBadRequest(post(API_URL), mvc, umaConfiguracaoAgendaRequest(ETipoConfiguracao.ESTRUTURA), erroEsperado);
    }

    @Test
    @WithMockUser(roles = PERMISSAO_GERENCIA)
    public void salvar_deveRetornarBadRequest_quandoNaoPassarParametroDoSubcanal() {
        var erroEsperado = jsonPath("$[*].message", containsInAnyOrder(
            "O campo subcanalId é obrigatório."
        ));
        isBadRequest(post(API_URL), mvc, umaConfiguracaoAgendaRequest(ETipoConfiguracao.SUBCANAL), erroEsperado);
    }

    @Test
    @WithMockUser
    public void salvar_deveRetornarForbidden_quandoNaoPossuirPermissao() {
        isForbidden(post(API_URL), mvc);
        verifyZeroInteractions(service);
    }

    @Test
    @WithAnonymousUser
    public void salvar_deveRetornarUnauthorized_quandoNaoAutenticado() {
        isUnauthorized(post(API_URL), mvc);
        verifyZeroInteractions(service);
    }

    @Test
    @WithMockUser
    public void getQtdHorasAdicionaisAgendaByUsuario_deveRetornarOk_quandoAutenticado() {
        isOk(get(API_URL.concat("/horas-adicionais"))
            .param("subcanalId", "1"), mvc);
        verify(service).getQtdHorasAdicionaisAgendaByUsuario(ETipoCanal.PAP.getId(), null);
    }

    @Test
    @WithAnonymousUser
    public void getQtdHorasAdicionaisAgendaByUsuario_deveRetornarUnauthorized_quandoNaoAutenticado() {
        isUnauthorized(get(API_URL.concat("/horas-adicionais")), mvc);
        verifyZeroInteractions(service);
    }
}
