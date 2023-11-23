package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.service.DiasUteisService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.exceptions.SubCanalCustomExceptionHandler;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(DiasUteisController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
    @MockBean(SubCanalCustomExceptionHandler.class),
})
@Import(OAuth2ResourceConfig.class)
public class DiasUteisControllerTest {

    private static final String API_URI = "/api/dias-uteis";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private DiasUteisService diasUteisService;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getDataComDiasUteisAdicionado_deveRetornarUnauthorizied_quandoNaoTiverUsuarioAutenticado() {
        mvc.perform(get(API_URI))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getDataComDiasUteisAdicionado_deveRetornarBadRequest_quandoFaltarCampoObrigatorio() {
        mvc.perform(get(API_URI)
            .param("cidadeId", "1111")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getDataComDiasUteisAdicionado_deveRetornarOk_quandoRequestTiverCamposObrigatorios() {
        mvc.perform(get(API_URI)
            .param("cidadeId", "1111")
            .param("dataOriginal", "2020-01-25T21:34:55")
            .param("qtdDiasUteisAdicionar", "3")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getDataComDiasUteisAdicionadoECidadeUf_deveRetornarUnauthorized_quandoNaoTiverUsuarioAutenticado() {
        mvc.perform(get(API_URI + "/cidade-uf"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getDataComDiasUteisAdicionadoECidadeUf_deveRetornarBadRequest_quandoFaltarCampoObrigatorio() {
        mvc.perform(get(API_URI + "/cidade-uf")
            .param("cidade", "LONDRINA")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getDataComDiasUteisAdicionadoECidadeUf_deveRetornarOk_quandoRequestTiverCamposObrigatorios() {
        mvc.perform(get(API_URI + "/cidade-uf")
            .param("cidade", "LONDRINA")
            .param("uf", "PR")
            .param("dataOriginal", "2020-01-25T21:34:55")
            .param("qtdDiasUteisAdicionar", "2")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
