package br.com.xbrain.autenticacao.modules.site.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.site.dto.SiteRequest;
import br.com.xbrain.autenticacao.modules.site.repository.SiteRepository;
import org.assertj.core.api.Assertions;
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

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelOperacaoGerente;
import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.OPERACAO_GERENTE_COMERCIAL;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql", "classpath:/tests_sites.sql"})
public class SiteControllerTest {

    private static final String API_URI = "/api/sites";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @Autowired
    private SiteRepository repository;

    @Test
    public void getAll_deveRetornarUnauthorized_quandoNaoInformarAToken() throws Exception {
        mvc.perform(get(API_URI)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAll_deveRetornarTodos_quandoOperacaoGerente() throws Exception {
        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(umUsuarioAutenticadoNivelOperacaoGerente());

        mvc.perform(get(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(4)))
                .andExpect(jsonPath("$.content[0].nome", is("São Paulo")))
                .andExpect(jsonPath("$.content[0].timeZone", is("AMERICA_SAO_PAULO")))
                .andExpect(jsonPath("$.content[1].nome", is("Recife")))
                .andExpect(jsonPath("$.content[1].timeZone", is("AMERICA_RECIFE")))
                .andExpect(jsonPath("$.content[2].nome", is("Manaus")))
                .andExpect(jsonPath("$.content[2].timeZone", is("AMERICA_MANAUS")))
                .andExpect(jsonPath("$.content[3].nome", is("Inativo")))
                .andExpect(jsonPath("$.content[3].timeZone", is("AMERICA_MANAUS")));
    }

    @Test
    public void findById_deveRetornarSaoPaulo_quandoOperacaoGerente() throws Exception {
        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(umUsuarioAutenticadoNivelOperacaoGerente());

        mvc.perform(get(API_URI + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("São Paulo")))
                .andExpect(jsonPath("$.cidadesIds", is(List.of(5578))))
                .andExpect(jsonPath("$.supervisoresIds", is(List.of(102))))
                .andExpect(jsonPath("$.coordenadoresIds", is(List.of(300))))
                .andExpect(jsonPath("$.timeZone", is("AMERICA_SAO_PAULO")));
    }

    @Test
    public void save_badRequest_quandoValidarOsCamposObrigatorios() throws Exception {
        mvc.perform(post(API_URI)
                .content(convertObjectToJsonBytes(new SiteRequest()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                        "O campo timeZone é obrigatório.",
                        "O campo nome é obrigatório.",
                        "O campo cidadesIds é obrigatório.",
                        "O campo supervisoresIds é obrigatório.",
                        "O campo coordenadoresIds é obrigatório.")));
    }

    @Test
    public void save_novoItem_quandoRequestCompleto() throws Exception {
        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(umUsuarioAutenticadoNivelOperacaoGerente());

        mvc.perform(post(API_URI)
                .content(convertObjectToJsonBytes(umSite()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    public void ativarSite_siteAtivo_quandoSiteInativo() throws Exception {
        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(umUsuarioAutenticadoNivelOperacaoGerente());

        mvc.perform(put(API_URI + "/4/ativar")
                .header("Authorization", getAccessToken(mvc, ADMIN)))
                .andExpect(status().isOk());

        Assertions.assertThat(repository.findById(4).orElseThrow())
                .extracting("situacao")
                .contains(ESituacao.A);
    }

    @Test
    public void inativarSite_siteInativo_quandoSiteAtivo() throws Exception {
        when(autenticacaoService.getUsuarioAutenticado())
                .thenReturn(umUsuarioAutenticadoNivelOperacaoGerente());

        mvc.perform(put(API_URI + "/1/inativar")
                .header("Authorization", getAccessToken(mvc, ADMIN)))
                .andExpect(status().isOk());

        Assertions.assertThat(repository.findById(1).orElseThrow())
                .extracting("situacao")
                .contains(ESituacao.I);
    }

    private SiteRequest umSite() {
        var request = new SiteRequest();
        request.setId(1);
        request.setNome("Cambé");
        request.setTimeZone(ETimeZone.BRT);
        request.setCidadesIds(List.of(4498));
        request.setCoordenadoresIds(List.of(102));
        request.setSupervisoresIds(List.of(300));
        return request;
    }
}
