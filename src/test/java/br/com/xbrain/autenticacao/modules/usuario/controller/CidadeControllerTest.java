package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeSiteResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadesUfsRequest;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import helpers.Usuarios;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static helpers.TestsHelper.*;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class CidadeControllerTest {

    @SpyBean
    private CidadeService cidadeService;
    @Autowired
    private MockMvc mvc;

    @Test
    public void deveRetornarTodosPorUf() throws Exception {
        mvc.perform(get("/api/cidades?idUf=1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(8)));
    }

    @Test
    public void deveRetornarCidadePorUfAndCidadeNome() throws Exception {
        mvc.perform(get("/api/cidades/uf-cidade/PR/LONDRINA")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("LONDRINA")));
    }

    @Test
    public void buscarCidadeUfIds_deveRetornarOsIdsDaCidadeEUf() throws Exception {
        mvc.perform(get("/api/cidades/uf-cidade-ids/PR/ARAPONGAS")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cidadeId", is(3237)))
            .andExpect(jsonPath("$.ufId", is(1)));
    }

    @Test
    public void getCidadeSubcluster_deveRetornarCidadeComSubClusterPorUfAndCidadeNome_seExistir() throws Exception {

        doReturn(umaCidade()).when(cidadeService).findByUfNomeAndCidadeNome(any(), any());
        mvc.perform(get("/api/cidades/recuperar-cidade/PR/LONDRINA")

                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idCidade", is(5578)))
            .andExpect(jsonPath("$.idSubcluster", is(189)))
            .andExpect(jsonPath("$.idUf", is(1)))
            .andExpect(jsonPath("$.nomeCidade", is("LONDRINA")))
            .andExpect(jsonPath("$.nomeUf", is("PARANA")));
    }

    @Test
    public void deveRetornarTodosPorSubCluster() throws Exception {
        mvc.perform(get("/api/cidades?idSubCluster=57")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].nome", is("JACARAU")))
            .andExpect(jsonPath("$[0].subCluster.nome", is("JOÃO PESSOA")))
            .andExpect(jsonPath("$[0].subCluster.cluster.nome", is("PARAÍBA")))
            .andExpect(jsonPath("$[0].subCluster.cluster.grupo.nome", is("NORDESTE")))
            .andExpect(jsonPath("$[0].subCluster.cluster.grupo.regional.nome", is("LESTE")));
    }

    @Test
    public void deveRetornarSomentePorSubClusterGerenteComercial() throws Exception {
        mvc.perform(get("/api/cidades?idSubCluster=189")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].nome", is("ARAPONGAS")))
            .andExpect(jsonPath("$[0].subCluster.nome", is("LONDRINA")))
            .andExpect(jsonPath("$[0].subCluster.cluster.nome", is("NORTE DO PARANÁ")))
            .andExpect(jsonPath("$[0].subCluster.cluster.grupo.nome", is("NORTE DO PARANÁ")))
            .andExpect(jsonPath("$[0].subCluster.cluster.grupo.regional.nome", is("SUL")));
    }

    @Test
    public void deveRetornarSomentePorRegionalIdGerenteComercial() throws Exception {
        mvc.perform(get("/api/cidades/regional/3")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].nomeCidade", is("LONDRINA")));
    }

    @Test
    public void deveRetornarTodosPorRegionalId() throws Exception {
        mvc.perform(get("/api/cidades/regional/1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void deveRetornarTodosPorGrupoId() throws Exception {
        mvc.perform(get("/api/cidades/grupo/1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void deveRetornarTodosPorClusterId() throws Exception {
        mvc.perform(get("/api/cidades/cluster/1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void deveRetornarTodosPorSubClusterId() throws Exception {
        mvc.perform(get("/api/cidades/sub-cluster/1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void getHierarquia_deveRetornarTodaEstruturaDeCluster_quandoPossuiEstrutura() throws Exception {
        mvc.perform(get("/api/cidades/5578/clusterizacao")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cidadeId", is(5578)))
            .andExpect(jsonPath("$.cidadeNome", is("LONDRINA")))
            .andExpect(jsonPath("$.subclusterId", is(189)))
            .andExpect(jsonPath("$.subclusterNome", is("LONDRINA")))
            .andExpect(jsonPath("$.clusterId", is(45)))
            .andExpect(jsonPath("$.clusterNome", is("NORTE DO PARANÁ")))
            .andExpect(jsonPath("$.grupoId", is(20)))
            .andExpect(jsonPath("$.grupoNome", is("NORTE DO PARANÁ")))
            .andExpect(jsonPath("$.regionalId", is(3)))
            .andExpect(jsonPath("$.regionalNome", is("SUL")));
    }

    @Test
    public void findAll_deveRetornarTodasAsCidadesNetUno_quandoNetUnoForTrue() throws Exception {
        mvc.perform(get("/api/cidades/net-uno")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(4498)))
            .andExpect(jsonPath("$[0].nome", is("CHAPECO")))
            .andExpect(jsonPath("$[0].netUno", is("V")))
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void buscarCidadesPorEstados_deveRetornarAsCidadesDeCadaEstado() throws Exception {
        mvc.perform(get("/api/cidades/por-estados")
                .param("estadosIds", "1", "2")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(13)))
            .andExpect(jsonPath("$[0].value", is(3237)))
            .andExpect(jsonPath("$[0].label", is("ARAPONGAS - PR")))
            .andExpect(jsonPath("$[1].value", is(4870)))
            .andExpect(jsonPath("$[1].label", is("BERNARDINO DE CAMPOS - SP")));
    }

    @Test
    public void getCidadeByCodigoCidadeDbm_deveRetornarCidade_quandoExistirCidadeComCodigoCidadeDbm() throws Exception {
        doReturn(umaCidadeComSite()).when(cidadeService).getCidadeByCodigoCidadeDbm(any());
        mvc.perform(get("/api/cidades/cidade-dbm/1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(5578)));
    }

    @Test
    public void getAllCidadeByUfs_deveRetornarTodasAsCidadesDoEstados_quandoExistir() throws Exception {
        mvc.perform(get("/api/cidades")
                .param("ufIds", "1,2")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(13)))
            .andExpect(jsonPath("$[0].cidade", is("ARAPONGAS")));
    }

    @Test
    public void findCidadeByCodigoIbge_deveRetornarCidade_quandoEncontrarPorCodigoIbge() throws Exception {
        mvc.perform(get("/api/cidades/codigo-ibge/{codigoIbge}", 4101507)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(3237)))
            .andExpect(jsonPath("$.nome", is("ARAPONGAS")))
            .andExpect(jsonPath("$.codigoIbge", is("4101507")));
    }

    @Test
    public void findCidadeByCodigoIbge_deveRetornar200ComResponseBodyVazio_quandoNaoEncontrarPorCodigoIbge() throws Exception {
        mvc.perform(get("/api/cidades/codigo-ibge/{codigoIbge}", 123456)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @SneakyThrows
    public void getCodigoIbgeRegionalByCidadeNomeAndUf_deveRetornarUnauthorized_quandoNaoInformarToken() {
        mvc.perform(post("/api/cidades/por-nome-e-ufs"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")))
            .andExpect(jsonPath("$.error_description", is("Full authentication is required to access this resource")));

        verify(cidadeService, never()).getCodigoIbgeRegionalByCidadeNomeAndUf(any(CidadesUfsRequest.class));
    }

    @Test
    @SneakyThrows
    public void getCodigoIbgeRegionalByCidadeNomeAndUf_deveRetornarUnauthorized_quandoInformarTokenComSenhaInvalida() {
        mvc.perform(post("/api/cidades/por-nome-e-ufs")
                .header("Authorization", getAccessTokenComSenhaInvalida(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("invalid_token")));

        verify(cidadeService, never()).getCodigoIbgeRegionalByCidadeNomeAndUf(any(CidadesUfsRequest.class));
    }

    @Test
    @SneakyThrows
    public void getCodigoIbgeRegionalByCidadeNomeAndUf_deveRetornarBadRequest_quandoNaoInformarListasDeCidadesEUfs() {
        mvc.perform(post("/api/cidades/por-nome-e-ufs")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(cidadeService, never()).getCodigoIbgeRegionalByCidadeNomeAndUf(any(CidadesUfsRequest.class));
    }

    @Test
    @SneakyThrows
    public void getCodigoIbgeRegionalByCidadeNomeAndUf_deveRetornarZero_quandoInformarListasVaziasDeCidadesEUfs() {
        mvc.perform(post("/api/cidades/por-nome-e-ufs")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(listasCidadesUfsVazia())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());

        verify(cidadeService, times(1)).getCodigoIbgeRegionalByCidadeNomeAndUf(eq(listasCidadesUfsVazia()));
    }

    @Test
    @SneakyThrows
    public void getCodigoIbgeRegionalByCidadeNomeAndUf_deveRetornarZero_quandoInformarListaComValoresInexistentes() {
        mvc.perform(post("/api/cidades/por-nome-e-ufs")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(listasCidadesUfsInvalida()))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());

        verify(cidadeService, times(1)).getCodigoIbgeRegionalByCidadeNomeAndUf(eq(listasCidadesUfsInvalida()));
    }

    @Test
    @SneakyThrows
    public void getCodigoIbgeRegionalByCidadeNomeAndUf_deveRetornarListaCodigoIbgeRegionalResponse_quandoListasForemValidas() {
        mvc.perform(post("/api/cidades/por-nome-e-ufs")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(listasCidadesUfsValida())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].cidadeId", is(2641)))
            .andExpect(jsonPath("$[0].cidadeNome", is("CARAUBAS")))
            .andExpect(jsonPath("$[0].codigoIbge", is("2504074")))
            .andExpect(jsonPath("$[0].regionalId", is(1)))
            .andExpect(jsonPath("$[0].regionalNome", is("LESTE")))
            .andExpect(jsonPath("$[0].ufId", is(24)))
            .andExpect(jsonPath("$[0].uf", is("PARAIBA")))
            .andExpect(jsonPath("$[0].ufSigla", is("PB")))
            .andExpect(jsonPath("$[1].cidadeId", is(5578)))
            .andExpect(jsonPath("$[1].cidadeNome", is("LONDRINA")))
            .andExpect(jsonPath("$[1].codigoIbge", is("4113700")))
            .andExpect(jsonPath("$[1].regionalId", is(3)))
            .andExpect(jsonPath("$[1].regionalNome", is("SUL")))
            .andExpect(jsonPath("$[1].ufId", is(1)))
            .andExpect(jsonPath("$[1].uf", is("PARANA")))
            .andExpect(jsonPath("$[1].ufSigla", is("PR")))
            .andExpect(jsonPath("$[2].cidadeId", is(5604)))
            .andExpect(jsonPath("$[2].cidadeNome", is("CARAUBAS")))
            .andExpect(jsonPath("$[2].codigoIbge", is("2402303")))
            .andExpect(jsonPath("$[2].regionalId", is(1)))
            .andExpect(jsonPath("$[2].regionalNome", is("LESTE")))
            .andExpect(jsonPath("$[2].ufId", is(26)))
            .andExpect(jsonPath("$[2].uf", is("RIO GRANDE DO NORTE")))
            .andExpect(jsonPath("$[2].ufSigla", is("RN")));

        verify(cidadeService, times(1)).getCodigoIbgeRegionalByCidadeNomeAndUf(eq(listasCidadesUfsValida()));
    }

    private Cidade umaCidade() {
        return Cidade.builder()
            .id(5578)
            .nome("LONDRINA")
            .subCluster(SubCluster.builder().id(189).build())
            .uf(Uf.builder().id(1).nome("PARANA").build())
            .build();
    }

    private CidadeSiteResponse umaCidadeComSite() {
        return CidadeSiteResponse.builder()
            .id(5578)
            .nome("LONDRINA")
            .siteId(189)
            .uf("pr")
            .build();
    }

    private CidadesUfsRequest listasCidadesUfsVazia() {
        return CidadesUfsRequest.builder()
            .cidades(List.of())
            .ufs(List.of())
            .build();
    }

    private CidadesUfsRequest listasCidadesUfsValida() {
        return CidadesUfsRequest.builder()
            .cidades(List.of("LONDRINA", "CARAUBAS"))
            .ufs(List.of("PR", "PB", "RN"))
            .build();
    }

    private CidadesUfsRequest listasCidadesUfsInvalida() {
        return CidadesUfsRequest.builder()
            .cidades(List.of("LONDRINA", "CARAUBAS"))
            .ufs(List.of("SP", "MG"))
            .build();
    }
}
