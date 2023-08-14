package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.dto.ImportacaoFeriadoHistoricoResponse;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service.ImportacaoAutomaticaFeriadoService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.OPERACAO_SUPERVISOR;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@WebMvcTest(ImportacaoAutomaticaFeriadoController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
})
@Import(OAuth2ResourceConfig.class)
public class ImportacaoAutomaticaFeriadoControllerTest {

    private static final String URL_BASE = "/api/importacao-automatica";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ImportacaoAutomaticaFeriadoService service;

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2050"})
    public void importarTodosOsFeriadosAnuais_deveImportarTodosOsFeriadosAnuais_seSolicitado() {
        mvc.perform(post(URL_BASE + "/importar-todos")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).importarTodosOsFeriadoAnuais();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void importarTodosOsFeriadosAnuais_deveLancarUnauthorized_seUsuarioNaoAutorizado() {
        mvc.perform(post(URL_BASE + "/importar-todos")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = OPERACAO_SUPERVISOR, roles = {"CTR_2033"})
    public void importarTodosOsFeriadosAnuais_deveLancarForbidden_seUsuarioSemPermissao() {
        mvc.perform(post(URL_BASE + "/importar-todos")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2050"})
    public void getAllHistoricoDeImportacao_deveRetornarPageDeHistoricos_seUsuarioPossuirPermissao() {
        when(service.getAllImportacaoHistorico(any(PageRequest.class), any(FeriadoFiltros.class)))
            .thenReturn(umaPageImportacaoHistorico());

        mvc.perform(get(URL_BASE + "/historico-importacao")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", not(empty())));

        verify(service).getAllImportacaoHistorico(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void getAllHistoricoDeImportacao_deveRetornarForbidden_seUsuarioNaoPossuirPermissao() {
        when(service.getAllImportacaoHistorico(any(PageRequest.class), any(FeriadoFiltros.class)))
            .thenReturn(umaPageImportacaoHistorico());

        mvc.perform(get(URL_BASE + "/historico-importacao")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(service);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllHistoricoDeImportacao_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        when(service.getAllImportacaoHistorico(any(PageRequest.class), any(FeriadoFiltros.class)))
            .thenReturn(umaPageImportacaoHistorico());

        mvc.perform(get(URL_BASE + "/historico-importacao")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(service);
    }

    private static Page<ImportacaoFeriadoHistoricoResponse> umaPageImportacaoHistorico() {
        return new PageImpl<>(
            List.of(umFeriadoImportacaoHistorico(1),
                umFeriadoImportacaoHistorico(2)
        ));
    }

    private static ImportacaoFeriadoHistoricoResponse umFeriadoImportacaoHistorico(Integer id) {
        return ImportacaoFeriadoHistoricoResponse.builder()
            .id(id)
            .situacaoFeriadoAutomacao(ESituacaoFeriadoAutomacao.IMPORTADO)
            .usuarioCadastroId(1)
            .build();
    }
}
