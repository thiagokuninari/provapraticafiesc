package br.com.xbrain.autenticacao.modules.site.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.site.dto.SiteRequest;
import br.com.xbrain.autenticacao.modules.site.repository.SiteRepository;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Sql({"classpath:/tests_database.sql", "classpath:/tests_sites.sql"})
public class SiteControllerTest {

    private static final String API_URI = "/api/sites";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private SiteRepository repository;

    @Test
    @SneakyThrows
    public void getSites_deveRetornarUnauthorized_quandoNaoInformarAToken() {
        mvc.perform(get(API_URI)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void getSites_deveRetornarTodos_quandoUsuarioTiverPermissaoDeVisualizarSites() {
        mvc.perform(get(API_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(4)))
            .andExpect(jsonPath("$.content[0].nome", is("São Paulo")))
            .andExpect(jsonPath("$.content[0].timeZone.descricao", is("Horário de Brasília")))
            .andExpect(jsonPath("$.content[0].timeZone.zoneId", is("America/Sao_Paulo")))
            .andExpect(jsonPath("$.content[0].timeZone.codigo", is("BRT")))
            .andExpect(jsonPath("$.content[1].nome", is("Rio Branco")))
            .andExpect(jsonPath("$.content[1].timeZone.descricao", is("Horário do Acre")))
            .andExpect(jsonPath("$.content[1].timeZone.zoneId", is("America/Rio_Branco")))
            .andExpect(jsonPath("$.content[1].timeZone.codigo", is("ACT")))
            .andExpect(jsonPath("$.content[2].nome", is("Manaus")))
            .andExpect(jsonPath("$.content[2].timeZone.descricao", is("Horário do Amazonas")))
            .andExpect(jsonPath("$.content[2].timeZone.zoneId", is("America/Manaus")))
            .andExpect(jsonPath("$.content[2].timeZone.codigo", is("AMT")))
            .andExpect(jsonPath("$.content[3].nome", is("Site Inativo")))
            .andExpect(jsonPath("$.content[3].timeZone.descricao",
                is("Horário de Fernando de Noronha")))
            .andExpect(jsonPath("$.content[3].timeZone.zoneId", is("America/Noronha")))
            .andExpect(jsonPath("$.content[3].timeZone.codigo", is("FNT")));
    }

    @Test
    @SneakyThrows
    public void getSites_forbidden_quandoUsuarioNaoTerPermissao() {
        mvc.perform(get(API_URI)
            .header("Authorization", getAccessToken(mvc, SOCIO_AA)))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void findById_deveRetornarSaoPaulo_quandoBuscarPorId() {
        mvc.perform(get(API_URI + "/100")
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(100)))
            .andExpect(jsonPath("$.nome", is("São Paulo")))
            .andExpect(jsonPath("$.cidadesIds", is(List.of(5578))))
            .andExpect(jsonPath("$.supervisoresIds", is(List.of(102))))
            .andExpect(jsonPath("$.coordenadoresIds", is(List.of(300))))
            .andExpect(jsonPath("$.timeZone.descricao", is("Horário de Brasília")))
            .andExpect(jsonPath("$.timeZone.zoneId", is("America/Sao_Paulo")))
            .andExpect(jsonPath("$.timeZone.codigo", is("BRT")));
    }

    @Test
    @SneakyThrows
    public void findById_notFound_quandoBuscarPorIdENaoExistir() {
        mvc.perform(get(API_URI + "/999")
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder("Site não encontrado.")));
    }

    @Test
    @SneakyThrows
    public void getAllAtivos_deveRetornarTodosOsSitesAtivos() {
        mvc.perform(get(API_URI + "/ativos")
            .header("Authorization", getAccessToken(mvc, OPERACAO_ASSISTENTE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].value", is(100)))
            .andExpect(jsonPath("$[0].label", is("São Paulo")))
            .andExpect(jsonPath("$[1].value", is(101)))
            .andExpect(jsonPath("$[1].label", is("Rio Branco")))
            .andExpect(jsonPath("$[2].value", is(102)))
            .andExpect(jsonPath("$[2].label", is("Manaus")));
    }

    @Test
    @SneakyThrows
    public void getAllSupervisoresBySiteId_deveRetornarOsSupervisoresDeAcordoComOSite() {
        mvc.perform(get(API_URI + "/{id}/supervisores", 101)
            .header("Authorization", getAccessToken(mvc, OPERACAO_ASSISTENTE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(102)))
            .andExpect(jsonPath("$[0].nome", is("Supervisor Operação")));
    }

    @Test
    @SneakyThrows
    public void buscarEstadosDisponiveis_estadosDisponiveis_quandoNaoTerVinculosComOutrosSites() {
        mvc.perform(get(API_URI + "/estados-disponiveis")
            .header("Authorization", getAccessToken(mvc, OPERACAO_ASSISTENTE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(17)));
    }

    @Test
    @SneakyThrows
    public void buscarCidadesDisponiveisPorEstadosIds_cidadesDisponiveis_quandoNaoVinculadasEmOutrosSites() {
        mvc.perform(get(API_URI + "/cidades-disponiveis")
            .param("estadosIds", "1")
            .header("Authorization", getAccessToken(mvc, OPERACAO_ASSISTENTE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(6)));
    }

    @Test
    @SneakyThrows
    public void buscarCidadesDisponiveisPorEstadosIds_cidadesDisponiveis_quandoNaoVinculadasEmOutrosSitesExcetoPorUm() {
        mvc.perform(get(API_URI + "/cidades-disponiveis")
            .param("estadosIds", "1")
            .param("siteIgnoradoId", "100")
            .header("Authorization", getAccessToken(mvc, OPERACAO_ASSISTENTE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(7)));
    }

    @Test
    @SneakyThrows
    public void save_badRequest_quandoValidarOsCamposObrigatorios() {
        mvc.perform(post(API_URI)
            .content(convertObjectToJsonBytes(new SiteRequest()))
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(5)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo estadosIds é obrigatório.",
                "O campo timeZone é obrigatório.",
                "O campo nome é obrigatório.",
                "O campo supervisoresIds é obrigatório.",
                "O campo coordenadoresIds é obrigatório.")));
    }

    @Test
    @SneakyThrows
    public void save_siteSalvo_quandoRequestCompleto() {
        mvc.perform(post(API_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umSiteRequest()))
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @SneakyThrows
    public void save_deveSalvarComTodasAsCidadesDisponiveisDoParana() {
        var request = umSiteRequest();
        request.setIncluirCidadesDisponiveis(true);

        mvc.perform(post(API_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(request))
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue()));

        mvc.perform(get(API_URI + "/cidades-disponiveis")
            .param("estadosIds", "1")
            .header("Authorization", getAccessToken(mvc, OPERACAO_ASSISTENTE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", empty()));
    }

    @Test
    @SneakyThrows
    public void save_badRequest_quandoExistirOutroSiteComNomeSemelhante() {
        var request = umSiteRequest();
        request.setNome("Sao Paulo");

        mvc.perform(post(API_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(request))
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder("Site já cadastrado no sistema.")));
    }

    @Test
    @SneakyThrows
    public void save_badRequest_quandoExistirCidadesVinculadasEmOutroSite() {
        var request = umSiteRequest();
        request.setCidadesIds(List.of(5578));

        mvc.perform(post(API_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(request))
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message",
                containsInAnyOrder("Existem cidades vinculadas à outro site.")));
    }

    @Test
    @SneakyThrows
    public void save_forbidden_quandoUsuarioNaoTerPermissaoGerenciarSites() {
        mvc.perform(post(API_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umSiteRequest()))
            .header("Authorization", getAccessToken(mvc, OPERACAO_ASSISTENTE)))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void getDetalheSiteById_deveRetornarUmSiteCompleto() {
        mvc.perform(get(API_URI + "/{id}/detalhe", 100)
            .header("Authorization", getAccessToken(mvc, OPERACAO_ASSISTENTE)))
            .andExpect(status().isOk())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(100)))
            .andExpect(jsonPath("$.nome", is("São Paulo")))
            .andExpect(jsonPath("$.situacao", is(ESituacao.A.name())))
            .andExpect(jsonPath("$.coordenadoresNomes", hasSize(1)))
            .andExpect(jsonPath("$.coordenadoresNomes[0]", is("Operacao Supervisor NET")))
            .andExpect(jsonPath("$.supervisoresNomes", hasSize(1)))
            .andExpect(jsonPath("$.supervisoresNomes[0]", is("Supervisor Operação")))
            .andExpect(jsonPath("$.estados", hasSize(1)))
            .andExpect(jsonPath("$.estados[0].id", is(1)))
            .andExpect(jsonPath("$.estados[0].nome", is("PARANA")))
            .andExpect(jsonPath("$.estados[0].uf", is("PR")))
            .andExpect(jsonPath("$.cidades", hasSize(1)))
            .andExpect(jsonPath("$.cidades[0].id", is(5578)))
            .andExpect(jsonPath("$.cidades[0].nome", is("LONDRINA")))
            .andExpect(jsonPath("$.timeZone.descricao", is("Horário de Brasília")))
            .andExpect(jsonPath("$.timeZone.zoneId", is("America/Sao_Paulo")))
            .andExpect(jsonPath("$.timeZone.codigo", is("BRT")));
    }

    @Test
    @SneakyThrows
    public void ativarSite_siteAtivo_quandoSiteInativo() {
        mvc.perform(put(API_URI + "/103/ativar")
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());

        assertThat(repository.findById(103).orElseThrow())
            .extracting("situacao")
            .contains(ESituacao.A);
    }

    @Test
    @SneakyThrows
    public void inativarSite_siteInativo_quandoSiteAtivo() {
        mvc.perform(put(API_URI + "/100/inativar")
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());

        assertThat(repository.findById(100).orElseThrow())
            .extracting("situacao")
            .contains(ESituacao.I);
    }

    private SiteRequest umSiteRequest() {
        return SiteRequest.builder()
            .nome("Arapa")
            .timeZone(ETimeZone.BRT)
            .estadosIds(List.of(1))
            .coordenadoresIds(List.of(102))
            .supervisoresIds(List.of(300))
            .cidadesIds(List.of(4498))
            .build();
    }
}
