package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoConfiguracao;
import br.com.xbrain.autenticacao.modules.usuario.exceptions.SubCanalCustomExceptionHandler;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.service.ConfiguracaoAgendaRealService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(CacheCleanController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
    @MockBean(SubCanalCustomExceptionHandler.class),
})
@Import(OAuth2ResourceConfig.class)
public class CacheCleanControllerTest {

    private static String API_CACHE = "/api/cache-clean";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ConfiguracaoAgendaRealService configuracaoAgendaRealService;
    @MockBean
    private AgenteAutorizadoService agenteAutorizadoService;
    @MockBean
    private AutenticacaoService autenticacaoService;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void limparCachesEstruturas_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(delete(API_CACHE + "/agente-autorizado/estrutura")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(autenticacaoService, never()).getUsuarioAutenticado();
        verify(agenteAutorizadoService, never()).flushCacheEstruturasAas();
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void limparCachesEstruturas_deveRetornarOk_seUsuarioAutenticadoENivelXbrain() {
        mockAutenticacao(umUsuarioAutenticado("XBRAIN"));
        mvc.perform(delete(API_CACHE + "/agente-autorizado/estrutura")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(agenteAutorizadoService).flushCacheEstruturasAas();
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void limparCachesEstruturas_deveRetornarForbidden_seNaoForNivelXbrain() {
        mockAutenticacao(umUsuarioAutenticado("OPERACAO"));
        mvc.perform(delete(API_CACHE + "/agente-autorizado/estrutura")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "Usuário sem permissão sobre a entidade requisitada.")));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(agenteAutorizadoService, never()).flushCacheEstruturasAas();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void limparCacheConfiguracaoAgenda_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(delete(API_CACHE + "/configuracao-agenda")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(autenticacaoService, never()).getUsuarioAutenticado();
        verify(configuracaoAgendaRealService, never()).flushCacheByTipoConfig(ETipoConfiguracao.NIVEL);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void limparCacheConfiguracaoAgenda_deveRetornarOk_seUsuarioAutenticadoENivelXbrain() {
        mockAutenticacao(umUsuarioAutenticado("XBRAIN"));
        mvc.perform(delete(API_CACHE + "/configuracao-agenda")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(configuracaoAgendaRealService).flushCacheByTipoConfig(ETipoConfiguracao.NIVEL);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void limparCacheConfiguracaoAgenda_deveRetornarForbidden_seNaoForNivelXbrain() {
        mockAutenticacao(umUsuarioAutenticado("OPERACAO"));
        mvc.perform(delete(API_CACHE + "/configuracao-agenda")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "Usuário sem permissão sobre a entidade requisitada.")));

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(configuracaoAgendaRealService, never()).flushCacheByTipoConfig(ETipoConfiguracao.NIVEL);
    }

    private void mockAutenticacao(UsuarioAutenticado usuarioAutenticado) {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticado);
    }

    private static UsuarioAutenticado umUsuarioAutenticado(String nivel) {
        return UsuarioAutenticado.builder()
            .usuario(Usuario.builder()
                .id(102)
                .canais(Set.of(ECanal.AGENTE_AUTORIZADO))
                .cargo(Cargo.builder()
                    .codigo(CodigoCargo.ADMINISTRADOR)
                    .nivel(Nivel.builder()
                        .codigo(CodigoNivel.XBRAIN)
                        .nome("XBRAIN")
                        .build())
                    .build())
                .build())
            .nivelCodigo(nivel)
            .canais(Set.of(ECanal.AGENTE_AUTORIZADO))
            .cargoCodigo(CodigoCargo.ADMINISTRADOR)
            .build();
    }
}
