package br.com.xbrain.autenticacao.modules.notificacao.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.notificacao.dto.BoaVindaAgenteAutorizadoRequest;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
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
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(NotificacaoController.class)
@MockBeans({
    @MockBean(UsuarioSubCanalObserver.class),
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class)
})
@Import(OAuth2ResourceConfig.class)
public class NotificacaoControllerTest {

    private static final String API_NOTIFICACAO = "/api/notificacao";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private NotificacaoService notificacaoService;

    @Test
    @SneakyThrows
    @WithMockUser
    public void save_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(post(API_NOTIFICACAO.concat("/boas-vindas-agente-autorizado"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(new BoaVindaAgenteAutorizadoRequest())))
            .andExpect(status().isOk());

        verify(notificacaoService).enviarEmailBoaVindaAgenteAutorizado(any());
    }

}
