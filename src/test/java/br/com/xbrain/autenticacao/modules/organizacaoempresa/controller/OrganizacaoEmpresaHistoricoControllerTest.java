package br.com.xbrain.autenticacao.modules.organizacaoempresa.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaHistoricoResponse;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EHistoricoAcao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.service.OrganizacaoEmpresaHistoricoService;
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

import java.time.LocalDateTime;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.ControllerTestHelper.umUsuarioAdminAutenticado;
import static br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.ControllerTestHelper.umUsuarioMsoConsultorAutenticado;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.HELP_DESK;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
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
public class OrganizacaoEmpresaHistoricoControllerTest {

    private static final String API_URI = "/api/organizacao-empresa-historico";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OrganizacaoEmpresaHistoricoService service;

    @MockBean
    private AutenticacaoService autenticacaoService;

    @Test
    @SneakyThrows
    public void getHistoricoDaOrganizacaoEmpresa_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_URI + "/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void getHistoricoDaOrganizacaoEmpresa_deveRetornarForbidden_seUsuarioNaoTiverPermissao() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioMsoConsultorAutenticado());

        mvc.perform(get(API_URI + "/{id}", 1)
                .header("Authorization", getAccessToken(mvc, HELP_DESK))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void getHistoricoDaOrganizacaoEmpresa_deveRetornarHistoricoOrganizacao_seUsuarioAutenticado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAdminAutenticado());

        when(service.obterHistoricoDaOrganizacaoEmpresa(any())).thenReturn(
            List.of(OrganizacaoEmpresaHistoricoResponse.builder()
                .usuarioNome("Thiago")
                .dataAlteracao(LocalDateTime.of(2022, 5, 15, 12, 0, 0))
                .observacao(EHistoricoAcao.EDICAO)
                .build())
        );

        mvc.perform(get(API_URI + "/{id}", 1)
            .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].usuarioNome", is("Thiago")))
            .andExpect(jsonPath("$[0].observacao", is("EDICAO")))
            .andExpect(jsonPath("$[0].dataAlteracao", is("2022-05-15T12:00:00")));
    }
}
