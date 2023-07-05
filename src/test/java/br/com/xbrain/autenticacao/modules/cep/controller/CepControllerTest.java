package br.com.xbrain.autenticacao.modules.cep.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.cep.client.ConsultaCepClient;
import br.com.xbrain.autenticacao.modules.cep.service.ConsultaCepService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
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

import java.util.List;

import static helpers.TestsHelper.convertObjectToJsonBytes;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = CepController.class)
@Import(OAuth2ResourceConfig.class)
@WithMockUser
public class CepControllerTest {

    private static final String API_URL = "/api/cep";
    @MockBean
    private ConsultaCepClient consultaCepClient;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ConsultaCepService service;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @MockBean
    private TokenStore tokenStore;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarCidadeEstado_deveRetornarOk_quandoNaoPassarToken() {
        mvc.perform(get(API_URL + "/86080-260")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).consultarCep("86080-260");
    }

    @Test
    @SneakyThrows
    public void buscarCidadeEstado_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(API_URL + "/86080-260")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).consultarCep("86080-260");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarCidadesPorCeps_deveRetornarOk_quandoNaoPassarToken() {
        mvc.perform(post(API_URL)
                .content(convertObjectToJsonBytes(List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).consultarCeps(List.of());
    }

    @Test
    @SneakyThrows
    public void buscarCidadesPorCeps_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(post(API_URL)
                .content(convertObjectToJsonBytes(List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).consultarCeps(List.of());
    }
}
