package br.com.xbrain.autenticacao.modules.horarioacesso.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.horarioacesso.service.HorarioAcessoService;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.horarioacesso.helper.HorarioHelper.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(HorarioAcessoController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class)
})
@Import(OAuth2ResourceConfig.class)
public class HorarioAcessoControllerTest {

    private static final String URL = "/api/horarios-acesso";
    private static final String GERENCIAR_HORARIO_ACESSO = "AUT_20009";
    private static final String VISUALIZAR_STATUS_HORARIO_ACESSO = "AUT_20024";
    public static final ValidacaoException CANAL_INVALIDO =
        new ValidacaoException("O canal informado não é válido.");

    @Autowired
    private MockMvc mvc;
    @MockBean
    private HorarioAcessoService service;
    @MockBean
    private UsuarioSubCanalObserver usuarioSubCanalObserver;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getHorariosAcesso_deveRetornarUnauthorized_quandoNaoHouverUsuarioAutenticado() {
        mvc.perform(get(URL)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getHorariosAcesso_deveRetornarForbidden_quandoUsuarioNaoPossuirPermissao() {
        mvc.perform(get(URL)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = { GERENCIAR_HORARIO_ACESSO })
    public void getHorarioAcesso_deveRetornarOk_quandoUsuarioPossuirPermissao() {
        when(service.getHorarioAcesso(anyInt())).thenReturn(umHorarioAcessoResponse());

        mvc.perform(get(URL + "/1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getHorarios_deveRetornarUnauthorized_quandoNaoHouverUsuarioAutenticado() {
        mvc.perform(get(URL)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getHorarios_deveRetornarForbidden_quandoUsuarioNaoPossuirPermissao() {
        mvc.perform(get(URL)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = { GERENCIAR_HORARIO_ACESSO })
    public void getHorarios_deveRetornarOk_quandoUsuarioPossuirPermissao() {
        when(service.getHorariosAcesso(any(PageRequest.class), any(HorarioAcessoFiltros.class)))
            .thenReturn(umaListaHorarioAcessoResponse());

        mvc.perform(get(URL)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getHistoricos_deveRetornarUnauthorized_quandoNaoHouverUsuarioAutenticado() {
        mvc.perform(get(URL + "/1/historico")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getHistoricos_deveRetornarForbidden_quandoUsuarioNaoPossuirPermissao() {
        mvc.perform(get(URL + "/1/historico")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = { GERENCIAR_HORARIO_ACESSO })
    public void getHistoricos_deveRetornarOk_quandoUsuarioPossuirPermissao() {
        when(service.getHistoricos(any(PageRequest.class), anyInt())).thenReturn(umaListaHorarioHistoricoResponse());

        mvc.perform(get(URL + "/1/historico")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getStatus_deveRetornarUnauthorized_quandoNaoHouverUsuarioAutenticado() {
        mvc.perform(get(URL + "/status")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getStatus_deveRetornarForbidden_quandoUsuarioNaoPossuirPermissao() {
        mvc.perform(get(URL + "/status")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = { VISUALIZAR_STATUS_HORARIO_ACESSO })
    public void getStatus_deveRetornarOk_quandoUsuarioPossuirPermissao() {
        when(service.getStatus(any())).thenReturn(false);

        mvc.perform(get(URL + "/status")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-Usuario-Canal", "ATIVO_PROPRIO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(false)));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = { VISUALIZAR_STATUS_HORARIO_ACESSO })
    public void getStatus_deveRetornarBadRequest_quandoCanalInformadoNaoForAtivoProprio() {
        when(service.getStatus(any())).thenThrow(CANAL_INVALIDO);

        mvc.perform(get(URL + "/status")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-Usuario-Canal", "D2D_PROPRIO"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].message", is("O canal informado não é válido.")));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getStatusComParametroSiteId_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(URL + "/status/100")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getStatusComParametroSiteId_deveRetornarForbidden_quandoUsuarioNaoPossuirPermissao() {
        mvc.perform(get(URL + "/status/100")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = { VISUALIZAR_STATUS_HORARIO_ACESSO })
    public void getStatusComParametroSiteId_deveRetornarBadRequest_quandoCanalInformadoNaoForAtivoProprio() {
        when(service.getStatus(any(), anyInt())).thenThrow(CANAL_INVALIDO);

        mvc.perform(get(URL + "/status/100")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-Usuario-Canal", "ATIVO_PROPRIO"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].message", is("O canal informado não é válido.")));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = { VISUALIZAR_STATUS_HORARIO_ACESSO })
    public void getStatusComParametroSiteId_deveRetornarFalse_seHorarioAtualNaoSeEncaixarEmNenhumDia() {
        when(service.getStatus(any(), anyInt())).thenReturn(false);

        mvc.perform(get(URL + "/status/100")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-Usuario-Canal", "ATIVO_PROPRIO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(false)));
    }

    private Page<HorarioAcessoResponse> umaListaHorarioAcessoResponse() {
        return new PageImpl<>(List.of(umHorarioAcessoResponse()));
    }

    private Page<HorarioAcessoResponse> umaListaHorarioHistoricoResponse() {
        return new PageImpl<>(List.of(umHorarioHistoricoResponse()));
    }

}
