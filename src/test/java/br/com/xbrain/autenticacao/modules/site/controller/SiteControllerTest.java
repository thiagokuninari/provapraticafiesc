package br.com.xbrain.autenticacao.modules.site.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.site.dto.SiteRequest;
import br.com.xbrain.autenticacao.modules.site.repository.SiteRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHierarquiaResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
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

import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
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
    @MockBean
    private UsuarioService usuarioService;

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
            .andExpect(jsonPath("$.content", hasSize(6)))
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
            .andExpect(jsonPath("$.content[2].discadoraId", is(8)))
            .andExpect(jsonPath("$.content[3].nome", is("Site Inativo")))
            .andExpect(jsonPath("$.content[3].timeZone.descricao",
                is("Horário de Fernando de Noronha")))
            .andExpect(jsonPath("$.content[3].timeZone.zoneId", is("America/Noronha")))
            .andExpect(jsonPath("$.content[3].timeZone.codigo", is("FNT")));
    }

    @Test
    @SneakyThrows
    public void getSites_deveRetornarApenasComDiscadora_quandoSitePossuirDiscadora() {
        mvc.perform(get(API_URI + "?discadoraId=8")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome", is("Manaus")));
    }

    @Test
    @SneakyThrows
    public void getSites_deveRetornarApenasSemDiscadora_quandoSitesNaoTiveremDiscadora() {
        mvc.perform(get(API_URI)
                .param("naoPossuiDiscadora", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.content[0].nome", is("São Paulo")))
                .andExpect(jsonPath("$.content[1].nome", is("Rio Branco")))
                .andExpect(jsonPath("$.content[2].nome", is("Site Inativo")))
                .andExpect(jsonPath("$.content[3].nome", is("Rio Branco")))
                .andExpect(jsonPath("$.content[4].nome", is("Manaus")));
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
            .andExpect(jsonPath("$", hasSize(5)))
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
    public void getAllSupervisoresByHierarquia_deveRetornarSupervisores_quandoRespeitarSiteAndUsuarioSuperiorId() {
        when(usuarioService.getIdsSubordinadosDaHierarquia(300, CodigoCargo.SUPERVISOR_OPERACAO.name()))
            .thenReturn(List.of(400, 102));

        mvc.perform(get(API_URI + "/{id}/supervisores/hierarquia/{usuarioSuperiorId}", 100, 300)
            .header("Authorization", getAccessToken(mvc, OPERACAO_ASSISTENTE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(102)))
            .andExpect(jsonPath("$[0].nome", is("Supervisor Operação")));
    }

    @Test
    @SneakyThrows
    public void buscarSitesVinculadosAoUsuarioLogado_deveRetornarSitesDoUsuarioLogado() {
        mvc.perform(get(API_URI + "/usuario-logado")
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(5)))
            .andExpect(jsonPath("$[0].value", is(100)))
            .andExpect(jsonPath("$[0].label", is("São Paulo")))
            .andExpect(jsonPath("$[1].value", is(101)))
            .andExpect(jsonPath("$[1].label", is("Rio Branco")))
            .andExpect(jsonPath("$[2].value", is(102)))
            .andExpect(jsonPath("$[2].label", is("Manaus")));
    }

    @Test
    @SneakyThrows
    public void getSitesByEstadoId_deveRetornarOsSitesQueEstaoNoMesmoEstado() {
        mvc.perform(get(API_URI + "/estado/{estadoId}", 2)
            .header("Authorization", getAccessToken(mvc, OPERACAO_ASSISTENTE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].value", is(101)))
            .andExpect(jsonPath("$[0].label", is("Rio Branco")))
            .andExpect(jsonPath("$[1].value", is(102)))
            .andExpect(jsonPath("$[1].label", is("Manaus")));
    }

    @Test
    @SneakyThrows
    public void buscarCidadesDisponiveisPorEstadosIds_cidadesDisponiveis_quandoNaoVinculadasEmOutrosSites() {
        mvc.perform(get(API_URI + "/cidades-disponiveis")
            .param("estadosIds", "1")
            .header("Authorization", getAccessToken(mvc, OPERACAO_ASSISTENTE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @SneakyThrows
    public void buscarCidadesSemSitePorUsuarioEUfE_cidadesDisponiveisEComSiteEditado_quandoEditarSite() {
        mvc.perform(get(API_URI + "/cidades-disponiveis")
            .param("estadosIds", "1")
            .param("siteIgnoradoId", "100")
            .header("Authorization", getAccessToken(mvc, OPERACAO_ASSISTENTE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
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
            .andExpect(jsonPath("$[*].message", containsInAnyOrder("Site já cadastrado anteriormente com esse nome.")));
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
    public void inativarSite_siteInativo_quandoSiteAtivo() {
        mvc.perform(put(API_URI + "/100/inativar")
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());

        assertThat(repository.findById(100).orElseThrow())
            .extracting("situacao")
            .contains(ESituacao.I);
    }

    @Test
    @SneakyThrows
    public void updateSite_siteAtualizadoExcetoDiscadoraId_quandoAtualizarDescricao() {
        var siteRequest = umSiteRequest();
        siteRequest.setId(102);
        siteRequest.setNome("Koba");

        mvc.perform(put(API_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(siteRequest))
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(102)))
            .andExpect(jsonPath("$.nome", is("Koba")))
            .andExpect(jsonPath("$.discadoraId", is(8)));

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

    @Test
    public void getSiteBySupervisorId_siteSp_quandoBuscarSitePeloSupervisorId() throws Exception {
        mvc.perform(get(API_URI + "/supervisor/{supervisorId}", 102)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", equalTo(100)))
            .andExpect(jsonPath("$.nome", equalTo("São Paulo")));
    }

    @Test
    @SneakyThrows
    public void buscarSitesPermitidos_sites_quandoUsuarioAutenticado() {
        when(usuarioService.getSuperioresDoUsuario(anyInt()))
            .thenReturn(List.of(UsuarioHierarquiaResponse.builder()
            .id(102).build()));
        mvc.perform(get(API_URI + "/permitidos")
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
    public void buscarEstadosDisponiveis_deveBuscarTodosestadosDisponiveis_quandoNaoTerVinculosComOutrosSitesEAdmin() {
        mvc.perform(get(API_URI + "/estados-disponiveis")
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @SneakyThrows
    public void buscarAssistentesDaHierarquiaDoUsuarioSuperiorId_unauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_URI + "/assistentes-da-hierarquia/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void buscarAssistentesDaHierarquiaDoUsuarioSuperiorId_forbidden_seUsuarioNaoPossuiPermissao() {
        mvc.perform(get(API_URI + "/assistentes-da-hierarquia/1")
            .header("Authorization", getAccessToken(mvc, SOCIO_AA)))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void buscarAssistentesDaHierarquiaDoUsuarioSuperiorId_ok_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_URI + "/assistentes-da-hierarquia/1")
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void buscarVendedoresDaHierarquiaDoUsuarioSuperiorId_unauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_URI + "/vendedores-da-hierarquia/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void buscarVendedoresDaHierarquiaDoUsuarioSuperiorId_forbidden_seUsuarioNaoPossuiPermissao() {
        mvc.perform(get(API_URI + "/vendedores-da-hierarquia/1")
            .header("Authorization", getAccessToken(mvc, SOCIO_AA)))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void buscarVendedoresDaHierarquiaDoUsuarioSuperiorId_ok_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_URI + "/vendedores-da-hierarquia/1")
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());
    }
}
