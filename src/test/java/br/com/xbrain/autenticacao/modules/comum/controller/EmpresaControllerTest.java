package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.service.EmpresaService;
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

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static helpers.Empresas.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(EmpresaController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
    @MockBean(SubCanalCustomExceptionHandler.class),
})
@Import(OAuth2ResourceConfig.class)
public class EmpresaControllerTest {

    private static final String URL = "/api/empresas";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private EmpresaService empresaService;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAll_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAll_deveRetornarOk_seUsuarioAutenticadoENaoInformarUnidadeNegocioId() {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAll_deveRetornarOk_seUsuarioAutenticadoEInformarUnidadeNegocioId() {
        mvc.perform(get(URL)
                .param("unidadeNegocioId", "1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findWithoutXbrain_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(URL + "/obter-sem-xbrain")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(empresaService, never()).findWithoutXbrain();
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findWithoutXbrain_deveRetornarOk_quandoUsuarioAutenticado() {
        var empresas = Stream.of(CLARO_MOVEL, CLARO_TV, NET, CLARO_RESIDENCIAL)
            .map(empresa -> new SelectResponse(empresa.getId(), empresa.getNome()))
            .collect(Collectors.toList());

        doReturn(empresas)
            .when(empresaService)
            .findWithoutXbrain();

        mvc.perform(get(URL + "/obter-sem-xbrain")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].value", is(1)))
            .andExpect(jsonPath("$[0].label", is("Claro Móvel")))
            .andExpect(jsonPath("$[1].value", is(2)))
            .andExpect(jsonPath("$[1].label", is("Claro TV")))
            .andExpect(jsonPath("$[2].value", is(3)))
            .andExpect(jsonPath("$[2].label", is("NET")))
            .andExpect(jsonPath("$[3].value", is(5)))
            .andExpect(jsonPath("$[3].label", is("Claro Residencial")));

        verify(empresaService).findWithoutXbrain();
    }

}
