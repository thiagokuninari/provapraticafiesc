package br.com.xbrain.autenticacao.modules.organizacaoempresa.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EModalidadeEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.service.ModalidadeEmpresaService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.ControllerTestHelper.umUsuarioAdminAutenticado;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class ModalidadeEmpresaControllerTest {

    private static final String API_URI = "/api/modalidade-empresa";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ModalidadeEmpresaService service;

    @MockBean
    private AutenticacaoService autenticacaoService;

    @Test
    @SneakyThrows
    public void buscarModalidadeEmpresa_deveRetornarTodasModalidades_seUsuarioAutorizado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAdminAutenticado());

        when(service.getAllModalidadeEmpresa()).thenReturn(
            List.of(SelectResponse.builder()
                    .label("1")
                    .value(EModalidadeEmpresa.PAP)
                    .build(),
                SelectResponse.builder()
                    .label("2")
                    .value(EModalidadeEmpresa.TELEVENDAS)
                    .build())
        );

        mvc.perform(get(API_URI)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].label", is("1")))
            .andExpect(jsonPath("$[0].value", is("PAP")))
            .andExpect(jsonPath("$[1].label", is("2")))
            .andExpect(jsonPath("$[1].value", is("TELEVENDAS")));
    }
}
