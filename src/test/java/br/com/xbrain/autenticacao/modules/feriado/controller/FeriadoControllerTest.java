package br.com.xbrain.autenticacao.modules.feriado.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import(OAuth2ResourceConfig.class)
@WebMvcTest(controllers = FeriadoController.class)
public class FeriadoControllerTest {

    private static final String URL_BASE = "/api/feriado";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private TokenStore tokenStore;
    @MockBean
    private FeriadoService service;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @MockBean
    private UsuarioSubCanalObserver usuarioSubCanalObserver;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void consultarFeriadoNacional_consulta_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(URL_BASE + "/consulta?data=07/09/2018")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).consulta(any());
    }

    @Test
    @SneakyThrows
    public void consultarFeriadoNacional_consulta_deveRetornarBadRequest_quandoDataNaoInformada() {
        mvc.perform(get(URL_BASE + "/consulta")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(service, never()).consulta(any());
    }

    @Test
    @SneakyThrows
    public void consultarFeriadoNacional_consulta_deveRetornarOK_quandoDadosValidos() {
        mvc.perform(get(URL_BASE + "/consulta")
                .param("data", "07/09/2018")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).consulta(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void consultarFeriadoNacional_feriadoNacional_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(URL_BASE + "/feriado-nacional")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).consulta(any());
    }

    @Test
    @SneakyThrows
    public void consultarFeriadoNacional_feriadoNacional_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(URL_BASE + "/feriado-nacional")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).consulta();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void consultarFeriadoEstadualPorDataAtual_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(URL_BASE + "/feriados-estaduais")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).buscarUfsFeriadosEstaduaisPorData();
    }

    @Test
    @SneakyThrows
    public void consultarFeriadoEstadualPorDataAtual_deveRetornarOK_quandoDadosValidos() {
        mvc.perform(get(URL_BASE + "/feriados-estaduais")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).buscarUfsFeriadosEstaduaisPorData();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void consultarFeriadosMunicipais_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(URL_BASE + "/feriados-municipais")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).buscarFeriadosMunicipaisPorDataAtualUfs();
    }

    @Test
    @SneakyThrows
    public void consultarFeriadosMunicipais_deveRetornarOK_quandoDadosValidos() {
        mvc.perform(get(URL_BASE + "/feriados-municipais")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).buscarFeriadosMunicipaisPorDataAtualUfs();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void consultaFeriadoComCidade_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(URL_BASE + "/consulta/{cidadeId}", 1)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).consulta(any(), any());
    }

    @Test
    @SneakyThrows
    public void consultaFeriadoComCidade_deveRetornarBadRequest_quandoDataNull() {
        mvc.perform(get(URL_BASE + "/consulta/{cidadeId}", 1)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(service, never()).consulta(any(), any());
    }

    @Test
    @SneakyThrows
    public void consultaFeriadoComCidade_deveRetornarOk_quandoDataStringBlank() {
        mvc.perform(get(URL_BASE + "/consulta/{cidadeId}", 1)
                .param("data", "  ")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).consulta("  ", 1);
    }

    @Test
    @SneakyThrows
    public void consultaFeriadoComCidade_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(URL_BASE + "/consulta/{cidadeId}", 1)
                .param("data", "07/09/2018")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).consulta("07/09/2018", 1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findAllByAnoAtual_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(URL_BASE)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).findAllByAnoAtual();
    }

    @Test
    @SneakyThrows
    public void findAllByAnoAtual_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(URL_BASE)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).findAllByAnoAtual();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void consultarFeriadoComCidadeUf_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(URL_BASE + "/cidade/Arapongas/PR")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).isFeriadoHojeNaCidadeUf(any(), any());
    }

    @Test
    @SneakyThrows
    public void consultarFeriadoComCidadeUf_deveRetornarOk_quandoParametrosValidos() {
        doReturn(true).when(service).isFeriadoHojeNaCidadeUf(anyString(), anyString());

        mvc.perform(get(URL_BASE + "/cidade/Arapongas/PR")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).isFeriadoHojeNaCidadeUf("Arapongas", "PR");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void cacheClearFeriados_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(delete(URL_BASE + "/cache/clear")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).flushCacheFeriados();
    }

    @Test
    @SneakyThrows
    public void cacheClearFeriados_deveRetornarOk_quandoDadosValidos() {
        doNothing().when(service).flushCacheFeriados();

        mvc.perform(delete(URL_BASE + "/cache/clear")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        verify(service).flushCacheFeriados();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarTotalDeFeriadosPorMesAno_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(get(URL_BASE + "/mes-ano/total-feriados")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).buscarTotalDeFeriadosPorMesAno();
    }

    @Test
    @SneakyThrows
    public void buscarTotalDeFeriadosPorMesAno_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(get(URL_BASE + "/mes-ano/total-feriados")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).buscarTotalDeFeriadosPorMesAno();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void isFeriadoComCidadeId_deveRetornarUnauthorized_quandoTokenInvalido() {
        mvc.perform(get(URL_BASE + "/1520/feriado")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyZeroInteractions(service);
    }

    @Test
    @SneakyThrows
    public  void isFeriadoComCidadeId_deveRetornarOk_quandoDadosOk() {
        mvc.perform(get(URL_BASE + "/1520/feriado")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).isFeriadoComCidadeId(1520);
    }
}
