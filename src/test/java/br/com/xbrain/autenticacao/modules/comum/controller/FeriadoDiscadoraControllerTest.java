package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FeriadoDiscadoraControllerTest {

    private static final String URL = "/api/public/discadora/feriados/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeriadoService feriadoService;

    @Before
    public void setup() {
        when(feriadoService.isFeriadoHojeNaCidadeUf(any(), any())).thenReturn(false);
    }

    @Test
    public void consultarFeriadoComCidadeUf_deveLancarExcecao_seUltimoParametroContiverPonto() {
        assertThatExceptionOfType(NestedServletException.class)
                .isThrownBy(() ->
                        mockMvc.perform(get(URL + "cidade/Arapongas/P.R")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andReturn()
                )
                .withMessageContaining("java.lang.NoClassDefFoundError: com/sun/activation/registries/LogSupport");
    }

    @Test
    public void consultarFeriadoComCidadeUf_deveRetornarOk_seParametrosValidos() throws Exception {
        mockMvc.perform(get(URL + "cidade/Arapongas/PR")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }
}