package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeSiteResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UfResponse;
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

import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
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
            .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
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
    public void deveRetornarSomentePorRegionalIdGerenteComercial() throws Exception {
        mvc.perform(get("/api/cidades/regional/1027")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].nomeCidade", is("LONDRINA")));
    }

    @Test
    public void deveRetornarTodosPorRegionalId() throws Exception {
        mvc.perform(get("/api/cidades/regional/1")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void deveRetornarTodosPorSubClusterId() throws Exception {
        mvc.perform(get("/api/cidades/sub-cluster/1")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
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
    public void getCidadesByRegionalReprocessamento_deveRetornar200() throws Exception {
        mvc.perform(get("/api/cidades/reprocessamento-regional")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .param("regionalId", "1027")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4)))
            .andExpect(jsonPath("$[0].nomeCidade", is("ARAPONGAS")))
            .andExpect(jsonPath("$[1].nomeCidade", is("CHAPECO")))
            .andExpect(jsonPath("$[2].nomeCidade", is("LONDRINA")))
            .andExpect(jsonPath("$[3].nomeCidade", is("MARINGA")));
    }

    @Test
    public void getCidadesByRegionalAndUfReprocessamento_deveRetornar200() throws Exception {
        mvc.perform(get("/api/cidades/reprocessamento-uf")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .param("regionalId", "1027")
            .param("ufId", "1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].nomeCidade", is("ARAPONGAS")))
            .andExpect(jsonPath("$[1].nomeCidade", is("LONDRINA")))
            .andExpect(jsonPath("$[2].nomeCidade", is("MARINGA")));
    }

    @SneakyThrows
    public void getCodigoIbgeRegionalByCidade_deveRetornarUnauthorized_quandoNaoInformarToken() {
        mvc.perform(get("/api/cidades/codigo-ibge/regional")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(cidadeService, never()).getCodigoIbgeRegionalByCidade(anyList());
    }

    @Test
    @SneakyThrows
    public void getCodigoIbgeRegionalByCidade_deveRetornarBadRequest_quandoNaoInformarListaDeCidadesId() {
        mvc.perform(get("/api/cidades/codigo-ibge/regional")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(cidadeService, never()).getCodigoIbgeRegionalByCidade(anyList());
    }

    @Test
    @SneakyThrows
    public void getCodigoIbgeRegionalByCidade_deveRetornarZero_quandoInformarListaVaziaDeCidadesId() {
        mvc.perform(get("/api/cidades/codigo-ibge/regional?cidadesId=")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(cidadeService, times(1)).getCodigoIbgeRegionalByCidade(eq(List.of()));
    }

    @Test
    @SneakyThrows
    public void getCodigoIbgeRegionalByCidade_deveRetornarZero_quandoInformarListaComCidadeIdNaoExistente() {
        mvc.perform(get("/api/cidades/codigo-ibge/regional")
            .param("cidadesId", "123123")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(cidadeService, times(1)).getCodigoIbgeRegionalByCidade(eq(List.of(123123)));
    }

    @Test
    @SneakyThrows
    public void getCodigoIbgeRegionalByCidade_deveRetornarListaCodigoIbgeRegionalResponse_quandoEncontrarPorCidadesId() {
        mvc.perform(get("/api/cidades/codigo-ibge/regional")
            .param("cidadesId", "3426, 5578")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].cidadeId", is(3426)))
            .andExpect(jsonPath("$[0].cidadeNome", is("MARINGA")))
            .andExpect(jsonPath("$[0].codigoIbge", is("4115200")))
            .andExpect(jsonPath("$[0].regionalId", is(1027)))
            .andExpect(jsonPath("$[0].regionalNome", is("RPS")))
            .andExpect(jsonPath("$[1].cidadeId", is(5578)))
            .andExpect(jsonPath("$[1].cidadeNome", is("LONDRINA")))
            .andExpect(jsonPath("$[1].codigoIbge", is("4113700")))
            .andExpect(jsonPath("$[1].regionalId", is(1027)))
            .andExpect(jsonPath("$[1].regionalNome", is("RPS")));

        verify(cidadeService, times(1)).getCodigoIbgeRegionalByCidade(eq(List.of(3426, 5578)));
    }

    @Test
    public void findCidadesByCodigosIbge_deveRetornarListaDeCidades_quandoEncontrarPorCodigosIbge() throws Exception {
        doReturn(umaListaCidadeResponse())
            .when(cidadeService)
            .findCidadesByCodigosIbge(List.of("4113700", "3527108"));

        mvc.perform(post("/api/cidades/codigos-ibge")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(List.of("4113700", "3527108")))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(5578)))
            .andExpect(jsonPath("$[0].nome", is("LONDRINA")))
            .andExpect(jsonPath("$[0].codigoIbge", is("4113700")))
            .andExpect(jsonPath("$[0].uf.nome", is("PR")))
            .andExpect(jsonPath("$[1].id", is(5579)))
            .andExpect(jsonPath("$[1].nome", is("LINS")))
            .andExpect(jsonPath("$[1].codigoIbge", is("3527108")))
            .andExpect(jsonPath("$[1].uf.nome", is("SP")));
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

    private List<CidadeResponse> umaListaCidadeResponse() {
        return List.of(
            CidadeResponse
                .builder()
                .id(5578)
                .nome("LONDRINA")
                .codigoIbge("4113700")
                .uf(
                    UfResponse
                        .builder()
                        .id(2)
                        .nome("PR")
                        .build()
                )
                .build(),
            CidadeResponse
                .builder()
                .id(5579)
                .nome("LINS")
                .codigoIbge("3527108")
                .uf(
                    UfResponse
                        .builder()
                        .id(1)
                        .nome("SP")
                        .build()
                )
                .build()
        );
    }
}
