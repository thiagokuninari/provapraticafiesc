package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;

import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.OPERACAO_ASSISTENTE;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Sql("classpath:/tests_database.sql")
public class CidadeControllerTest {

    private static final String URL = "/api/cidades";

    @SpyBean
    private CidadeService cidadeService;
    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    public void deveRetornarTodosPorUf() {
        mvc.perform(get(URL + "?idUf=1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(19)));
    }

    @Test
    @SneakyThrows
    public void deveRetornarCidadePorUfAndCidadeNome() {
        mvc.perform(get(URL + "/uf-cidade/PR/LONDRINA")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("LONDRINA")));
    }

    @Test
    @SneakyThrows
    public void buscarCidadeUfIds_deveRetornarOsIdsDaCidadeEUf() {
        mvc.perform(get(URL + "/uf-cidade-ids/PR/ARAPONGAS")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cidadeId", is(3237)))
            .andExpect(jsonPath("$.ufId", is(1)));
    }

    @Test
    @SneakyThrows
    public void getCidadeSubcluster_deveRetornarCidadeComSubClusterPorUfAndCidadeNome_seExistir() {
        mvc.perform(get(URL + "/recuperar-cidade/PR/LONDRINA")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idCidade", is(5578)))
            .andExpect(jsonPath("$.idSubcluster", is(189)))
            .andExpect(jsonPath("$.idUf", is(1)))
            .andExpect(jsonPath("$.nomeCidade", is("LONDRINA")))
            .andExpect(jsonPath("$.nomeUf", is("PARANA")));

        verify(cidadeService)
            .findByUfNomeAndCidadeNome("PR", "LONDRINA");
    }

    @Test
    @SneakyThrows
    public void deveRetornarTodosPorSubCluster() {
        mvc.perform(get(URL + "?idSubCluster=57")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].nome", is("JACARAU")));
    }

    @Test
    @SneakyThrows
    public void deveRetornarSomentePorSubClusterGerenteComercial() {
        mvc.perform(get(URL + "?idSubCluster=189")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].nome", is("ARAPONGAS")))
            .andExpect(jsonPath("$[1].nome", is("LONDRINA")));
    }

    @Test
    @SneakyThrows
    public void deveRetornarSomentePorRegionalIdGerenteComercial() {
        mvc.perform(get(URL + "/regional/3")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].nomeCidade", is("LONDRINA")));
    }

    @Test
    @SneakyThrows
    public void deveRetornarTodosPorRegionalId() {
        mvc.perform(get(URL + "/regional/1")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void deveRetornarTodosPorGrupoId() {
        mvc.perform(get(URL + "/grupo/1")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void deveRetornarTodosPorClusterId() {
        mvc.perform(get(URL + "/cluster/1")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void deveRetornarTodosPorSubClusterId() {
        mvc.perform(get(URL + "/sub-cluster/1")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void getHierarquia_deveRetornarTodaEstruturaDeCluster_quandoPossuiEstrutura() {
        mvc.perform(get(URL + "/5578/clusterizacao")
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
    @SneakyThrows
    public void findAll_deveRetornarTodasAsCidadesNetUno_quandoNetUnoForTrue() {
        mvc.perform(get(URL + "/net-uno")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(4498)))
            .andExpect(jsonPath("$[0].nome", is("CHAPECO")))
            .andExpect(jsonPath("$[0].netUno", is("V")))
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @SneakyThrows
    public void buscarCidadesPorEstados_deveRetornarBadRequest_quandoNaoInformarEstadosIds() {
        mvc.perform(get(URL + "/por-estados")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(cidadeService, never())
            .buscarCidadesPorEstadosIds(any());
    }

    @Test
    @SneakyThrows
    public void buscarCidadesPorEstados_deveRetornarOk_quandoInformarListaVaziaDeEstadosIds() {
        mvc.perform(get(URL + "/por-estados")
                .param("estadosIds", "")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());

        verify(cidadeService)
            .buscarCidadesPorEstadosIds(List.of());
    }

    @Test
    @SneakyThrows
    public void buscarCidadesPorEstados_deveRetornarOk_quandoInformarEstadosIds() {
        mvc.perform(get(URL + "/por-estados")
                .param("estadosIds", "3", "22")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(8)))
            .andExpect(jsonPath("$[0].value", is(34178)))
            .andExpect(jsonPath("$[0].label", is("ALTO DA SERRA - CHAPECO - SC")))
            .andExpect(jsonPath("$[1].value", is(4498)))
            .andExpect(jsonPath("$[1].label", is("CHAPECO - SC")))
            .andExpect(jsonPath("$[2].value", is(4505)))
            .andExpect(jsonPath("$[2].label", is("CORUPA - SC")))
            .andExpect(jsonPath("$[3].value", is(34164)))
            .andExpect(jsonPath("$[3].label", is("FIGUEIRA - CHAPECO - SC")))
            .andExpect(jsonPath("$[4].value", is(34116)))
            .andExpect(jsonPath("$[4].label", is("GOIO-EN - CHAPECO - SC")))
            .andExpect(jsonPath("$[5].value", is(34093)))
            .andExpect(jsonPath("$[5].label", is("MARECHAL BORMANN - CHAPECO - SC")))
            .andExpect(jsonPath("$[6].value", is(879)))
            .andExpect(jsonPath("$[6].label", is("MARILANDIA - ES")))
            .andExpect(jsonPath("$[7].value", is(33096)))
            .andExpect(jsonPath("$[7].label", is("SAPUCAIA - MARILANDIA - ES")));

        verify(cidadeService)
            .buscarCidadesPorEstadosIds(List.of(3, 22));
    }

    @Test
    @SneakyThrows
    public void getCidadeByCodigoCidadeDbm_deveRetornarCidade_quandoExistirCidadeComCodigoCidadeDbm() {
        mvc.perform(get(URL + "/cidade-dbm/91")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(3426)))
            .andExpect(jsonPath("$.siteId", is(110)))
            .andExpect(jsonPath("$.nome", is("MARINGA")))
            .andExpect(jsonPath("$.uf", is("PR")));

        verify(cidadeService)
            .getCidadeByCodigoCidadeDbm(91);
    }

    @Test
    @SneakyThrows
    public void getAllCidadeByUfs_deveRetornarOk_quandoInformarListaVaziaDeUfIds() {
        mvc.perform(get(URL)
                .param("ufIds", "")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());

        verify(cidadeService)
            .getAllCidadeByUfs(List.of());
    }

    @Test
    @SneakyThrows
    public void getAllCidadeByUfs_deveRetornarOk_quandoInformarListaDeUfIds() {
        mvc.perform(get(URL)
                .param("ufIds", "3, 22")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(8)))
            .andExpect(jsonPath("$[0].cidade", is("ALTO DA SERRA")))
            .andExpect(jsonPath("$[0].cidadePai", is("CHAPECO")))
            .andExpect(jsonPath("$[1].cidade", is("CHAPECO")))
            .andExpect(jsonPath("$[1].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[2].cidade", is("CORUPA")))
            .andExpect(jsonPath("$[2].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[3].cidade", is("FIGUEIRA")))
            .andExpect(jsonPath("$[3].cidadePai", is("CHAPECO")))
            .andExpect(jsonPath("$[4].cidade", is("GOIO-EN")))
            .andExpect(jsonPath("$[4].cidadePai", is("CHAPECO")))
            .andExpect(jsonPath("$[5].cidade", is("MARECHAL BORMANN")))
            .andExpect(jsonPath("$[5].cidadePai", is("CHAPECO")))
            .andExpect(jsonPath("$[6].cidade", is("MARILANDIA")))
            .andExpect(jsonPath("$[6].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[7].cidade", is("SAPUCAIA")))
            .andExpect(jsonPath("$[7].cidadePai", is("MARILANDIA")));

        verify(cidadeService)
            .getAllCidadeByUfs(List.of(3, 22));
    }

    @Test
    @SneakyThrows
    public void findCidadeByCodigoIbge_deveRetornarCidade_quandoEncontrarPorCodigoIbge() {
        mvc.perform(get(URL + "/codigo-ibge/{codigoIbge}", 4101507)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(3237)))
            .andExpect(jsonPath("$.nome", is("ARAPONGAS")))
            .andExpect(jsonPath("$.codigoIbge", is("4101507")));
    }

    @Test
    @SneakyThrows
    public void findCidadeByCodigoIbge_deveRetornar200ComResponseBodyVazio_quandoNaoEncontrarPorCodigoIbge() {
        mvc.perform(get(URL + "/codigo-ibge/{codigoIbge}", 123456)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @SneakyThrows
    public void getCidadesByRegionalReprocessamento_deveRetornar200() {
        mvc.perform(get(URL + "/reprocessamento-regional")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .param("regionalId", "1027")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(10)))
            .andExpect(jsonPath("$[0].nomeCidade", is("ARAPONGAS")))
            .andExpect(jsonPath("$[1].nomeCidade", is("CHAPECO")))
            .andExpect(jsonPath("$[2].nomeCidade", is("CORUMBATAI DO SUL")))
            .andExpect(jsonPath("$[3].nomeCidade", is("CORUPA")))
            .andExpect(jsonPath("$[4].nomeCidade", is("LOANDA")))
            .andExpect(jsonPath("$[5].nomeCidade", is("LOBATO")))
            .andExpect(jsonPath("$[6].nomeCidade", is("LONDRINA")))
            .andExpect(jsonPath("$[7].nomeCidade", is("MARILANDIA DO SUL")))
            .andExpect(jsonPath("$[8].nomeCidade", is("MARILUZ")))
            .andExpect(jsonPath("$[9].nomeCidade", is("MARINGA")));

        verify(cidadeService)
            .getCidadesByRegionalReprocessamento(1027);
    }

    @Test
    @SneakyThrows
    public void getCidadesByRegionalAndUfReprocessamento_deveRetornar200() {
        mvc.perform(get(URL + "/reprocessamento-uf")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .param("regionalId", "1027")
                .param("ufId", "1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(8)))
            .andExpect(jsonPath("$[0].nomeCidade", is("ARAPONGAS")))
            .andExpect(jsonPath("$[1].nomeCidade", is("CORUMBATAI DO SUL")))
            .andExpect(jsonPath("$[2].nomeCidade", is("LOANDA")))
            .andExpect(jsonPath("$[3].nomeCidade", is("LOBATO")))
            .andExpect(jsonPath("$[4].nomeCidade", is("LONDRINA")))
            .andExpect(jsonPath("$[5].nomeCidade", is("MARILANDIA DO SUL")))
            .andExpect(jsonPath("$[6].nomeCidade", is("MARILUZ")))
            .andExpect(jsonPath("$[7].nomeCidade", is("MARINGA")));

        verify(cidadeService)
            .getCidadesByRegionalAndUfReprocessamento(1027, 1);
    }

    @Test
    @SneakyThrows
    public void getCodigoIbgeRegionalByCidade_deveRetornarUnauthorized_quandoNaoInformarToken() {
        mvc.perform(get(URL + "/codigo-ibge/regional")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyZeroInteractions(cidadeService);
    }

    @Test
    @SneakyThrows
    public void getCodigoIbgeRegionalByCidade_deveRetornarBadRequest_quandoNaoInformarListaDeCidadesId() {
        mvc.perform(get(URL + "/codigo-ibge/regional")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verifyZeroInteractions(cidadeService);
    }

    @Test
    @SneakyThrows
    public void getCodigoIbgeRegionalByCidade_deveRetornarZero_quandoInformarListaVaziaDeCidadesId() {
        mvc.perform(get(URL + "/codigo-ibge/regional?cidadesId=")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(cidadeService)
            .getCodigoIbgeRegionalByCidade(List.of());
    }

    @Test
    @SneakyThrows
    public void getCodigoIbgeRegionalByCidade_deveRetornarZero_quandoInformarListaComCidadeIdNaoExistente() {
        mvc.perform(get(URL + "/codigo-ibge/regional")
                .param("cidadesId", "123123")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(cidadeService)
            .getCodigoIbgeRegionalByCidade(List.of(123123));
    }

    @Test
    @SneakyThrows
    public void getCodigoIbgeRegionalByCidade_deveRetornarListaCodigoIbgeRegionalResponse_quandoEncontrarPorCidadesId() {
        mvc.perform(get(URL + "/codigo-ibge/regional")
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

        verify(cidadeService)
            .getCodigoIbgeRegionalByCidade(List.of(3426, 5578));
    }

    @Test
    @SneakyThrows
    public void findCidadesByCodigosIbge_deveRetornarListaDeCidades_quandoEncontrarPorCodigosIbge() {
        var codigosIbge = List.of("4113700", "3527108");

        mvc.perform(post(URL + "/codigos-ibge")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(codigosIbge))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(5107)))
            .andExpect(jsonPath("$[0].nome", is("LINS")))
            .andExpect(jsonPath("$[0].codigoIbge", is("3527108")))
            .andExpect(jsonPath("$[0].uf.uf", is("SP")))
            .andExpect(jsonPath("$[1].id", is(5578)))
            .andExpect(jsonPath("$[1].nome", is("LONDRINA")))
            .andExpect(jsonPath("$[1].codigoIbge", is("4113700")))
            .andExpect(jsonPath("$[1].uf.uf", is("PR")));

        verify(cidadeService)
            .findCidadesByCodigosIbge(codigosIbge);
    }

    @Test
    @SneakyThrows
    public void getAllCidades_deveRetornarOk_quandoNaoInformarParametros() {
        mvc.perform(get(URL + "/todas")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(108)));

        verify(cidadeService)
            .getAll(null, null);
    }

    @Test
    @SneakyThrows
    public void getAllCidades_deveRetornarOk_quandoNaoExistirPorRegionalId() {
        mvc.perform(get(URL + "/todas")
                .param("regionalId", "500")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(cidadeService)
            .getAll(500, null);
    }

    @Test
    @SneakyThrows
    public void getAllCidades_deveRetornarOk_quandoNaoExistirPorUfId() {
        mvc.perform(get(URL + "/todas")
                .param("ufId", "50")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(cidadeService)
            .getAll(null, 50);
    }

    @Test
    @SneakyThrows
    public void getAllCidades_deveRetornarOk_quandoNaoExistirPorRegionalIdComUfId() {
        mvc.perform(get(URL + "/todas")
                .param("regionalId", "500")
                .param("ufId", "50")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(cidadeService)
            .getAll(500, 50);
    }

    @Test
    @SneakyThrows
    public void getAllCidades_deveRetornarOk_quandoInformarApenasRegionalId() {
        mvc.perform(get(URL + "/todas")
                .param("regionalId", "1030")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].id", is(4864)))
            .andExpect(jsonPath("$[0].nome", is("BARUERI")))
            .andExpect(jsonPath("$[0].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[1].id", is(4903)))
            .andExpect(jsonPath("$[1].nome", is("CAJAMAR")))
            .andExpect(jsonPath("$[1].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[2].id", is(5189)))
            .andExpect(jsonPath("$[2].nome", is("OSASCO")))
            .andExpect(jsonPath("$[2].cidadePai", is(nullValue())));

        verify(cidadeService)
            .getAll(1030, null);
    }

    @Test
    @SneakyThrows
    public void getAllCidades_deveRetornarOk_quandoInformarApenasUfId() {
        mvc.perform(get(URL + "/todas")
                .param("ufId", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(20)))
            .andExpect(jsonPath("$[0].nome", is("ALDEIA")))
            .andExpect(jsonPath("$[0].cidadePai", is("BARUERI")))
            .andExpect(jsonPath("$[1].nome", is("AMADEU AMARAL")))
            .andExpect(jsonPath("$[1].cidadePai", is("MARILIA")))
            .andExpect(jsonPath("$[2].nome", is("AVENCAS")))
            .andExpect(jsonPath("$[2].cidadePai", is("MARILIA")))
            .andExpect(jsonPath("$[3].nome", is("BARUERI")))
            .andExpect(jsonPath("$[3].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[4].nome", is("BERNARDINO DE CAMPOS")))
            .andExpect(jsonPath("$[4].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[5].nome", is("CAJAMAR")))
            .andExpect(jsonPath("$[5].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[6].nome", is("COSMOPOLIS")))
            .andExpect(jsonPath("$[6].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[7].nome", is("COSMORAMA")))
            .andExpect(jsonPath("$[7].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[8].nome", is("DIRCEU")))
            .andExpect(jsonPath("$[8].cidadePai", is("MARILIA")))
            .andExpect(jsonPath("$[9].nome", is("GUAPIRANGA")))
            .andExpect(jsonPath("$[9].cidadePai", is("LINS")))
            .andExpect(jsonPath("$[10].nome", is("JARDIM BELVAL")))
            .andExpect(jsonPath("$[10].cidadePai", is("BARUERI")))
            .andExpect(jsonPath("$[11].nome", is("JARDIM SILVEIRA")))
            .andExpect(jsonPath("$[11].cidadePai", is("BARUERI")))
            .andExpect(jsonPath("$[12].nome", is("JORDANESIA")))
            .andExpect(jsonPath("$[12].cidadePai", is("CAJAMAR")))
            .andExpect(jsonPath("$[13].nome", is("LACIO")))
            .andExpect(jsonPath("$[13].cidadePai", is("MARILIA")))
            .andExpect(jsonPath("$[14].nome", is("LINS")))
            .andExpect(jsonPath("$[14].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[15].nome", is("MARILIA")))
            .andExpect(jsonPath("$[15].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[16].nome", is("OSASCO")))
            .andExpect(jsonPath("$[16].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[17].nome", is("PADRE NOBREGA")))
            .andExpect(jsonPath("$[17].cidadePai", is("MARILIA")))
            .andExpect(jsonPath("$[18].nome", is("POLVILHO")))
            .andExpect(jsonPath("$[18].cidadePai", is("CAJAMAR")))
            .andExpect(jsonPath("$[19].nome", is("ROSALIA")))
            .andExpect(jsonPath("$[19].cidadePai", is("MARILIA")));

        verify(cidadeService)
            .getAll(null, 2);
    }

    @Test
    @SneakyThrows
    public void getAllCidades_deveRetornarOk_quandoInformarRegionalIdAndUfId() {
        mvc.perform(get(URL + "/todas")
                .param("regionalId", "1031")
                .param("ufId", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(17)))
            .andExpect(jsonPath("$[0].nome", is("ALDEIA")))
            .andExpect(jsonPath("$[0].cidadePai", is("BARUERI")))
            .andExpect(jsonPath("$[1].nome", is("AMADEU AMARAL")))
            .andExpect(jsonPath("$[1].cidadePai", is("MARILIA")))
            .andExpect(jsonPath("$[2].nome", is("AVENCAS")))
            .andExpect(jsonPath("$[2].cidadePai", is("MARILIA")))
            .andExpect(jsonPath("$[3].nome", is("BERNARDINO DE CAMPOS")))
            .andExpect(jsonPath("$[3].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[4].nome", is("COSMOPOLIS")))
            .andExpect(jsonPath("$[4].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[5].nome", is("COSMORAMA")))
            .andExpect(jsonPath("$[5].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[6].nome", is("DIRCEU")))
            .andExpect(jsonPath("$[6].cidadePai", is("MARILIA")))
            .andExpect(jsonPath("$[7].nome", is("GUAPIRANGA")))
            .andExpect(jsonPath("$[7].cidadePai", is("LINS")))
            .andExpect(jsonPath("$[8].nome", is("JARDIM BELVAL")))
            .andExpect(jsonPath("$[8].cidadePai", is("BARUERI")))
            .andExpect(jsonPath("$[9].nome", is("JARDIM SILVEIRA")))
            .andExpect(jsonPath("$[9].cidadePai", is("BARUERI")))
            .andExpect(jsonPath("$[10].nome", is("JORDANESIA")))
            .andExpect(jsonPath("$[10].cidadePai", is("CAJAMAR")))
            .andExpect(jsonPath("$[11].nome", is("LACIO")))
            .andExpect(jsonPath("$[11].cidadePai", is("MARILIA")))
            .andExpect(jsonPath("$[12].nome", is("LINS")))
            .andExpect(jsonPath("$[12].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[13].nome", is("MARILIA")))
            .andExpect(jsonPath("$[13].cidadePai", is(nullValue())))
            .andExpect(jsonPath("$[14].nome", is("PADRE NOBREGA")))
            .andExpect(jsonPath("$[14].cidadePai", is("MARILIA")))
            .andExpect(jsonPath("$[15].nome", is("POLVILHO")))
            .andExpect(jsonPath("$[15].cidadePai", is("CAJAMAR")))
            .andExpect(jsonPath("$[16].nome", is("ROSALIA")))
            .andExpect(jsonPath("$[16].cidadePai", is("MARILIA")));

        verify(cidadeService)
            .getAll(1031, 2);
    }

    @Test
    @SneakyThrows
    public void getCidadeById_deveRetornarBadRequest_quandoNaoEncontrarCidadePorId() {
        mvc.perform(get(URL + "/{cidadeId}", 15000)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder("Cidade não encontrada.")));

        verify(cidadeService)
            .getCidadeById(15000);
    }

    @Test
    @SneakyThrows
    public void getCidadeById_deveRetornarOk_quandoEncontrarCidadePorIdSemNomeCidadePai() {
        mvc.perform(get(URL + "/{cidadeId}", 5578)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(5578)))
            .andExpect(jsonPath("$.nome", is("LONDRINA")))
            .andExpect(jsonPath("$.uf.id", is(1)))
            .andExpect(jsonPath("$.uf.nome", is("PARANA")))
            .andExpect(jsonPath("$.regional.id", is(1027)))
            .andExpect(jsonPath("$.regional.nome", is("RPS")))
            .andExpect(jsonPath("$.fkCidade", is(nullValue())))
            .andExpect(jsonPath("$.cidadePai", is(nullValue())));

        verify(cidadeService)
            .getCidadeById(5578);
    }

    @Test
    @SneakyThrows
    public void getCidadeById_deveRetornarOk_quandoEncontrarCidadePorIdComNomeCidadePai() {
        mvc.perform(get(URL + "/{cidadeId}", 30910)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(30910)))
            .andExpect(jsonPath("$.nome", is("WARTA")))
            .andExpect(jsonPath("$.uf.id", is(1)))
            .andExpect(jsonPath("$.uf.nome", is("PARANA")))
            .andExpect(jsonPath("$.regional.id", is(1027)))
            .andExpect(jsonPath("$.regional.nome", is("RPS")))
            .andExpect(jsonPath("$.fkCidade", is(5578)))
            .andExpect(jsonPath("$.cidadePai", is("LONDRINA")));

        verify(cidadeService)
            .getCidadeById(30910);
    }

    @Test
    @SneakyThrows
    public void getCidadesDistritos_deveRetornarOk_quandoNaoInformarParametro() {
        //Consulta sem cache
        var result = mvc.perform(get(URL + "/distritos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        var map = new ObjectMapper().readValue(result, Map.class);

        assertThat(map.size(), is(108));

        verify(cidadeService)
            .getCidadesDistritos(null);

        //Consulta usando informações no cache
        var resultCache = mvc.perform(get(URL + "/distritos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        var mapCache = new ObjectMapper().readValue(resultCache, Map.class);

        assertThat(mapCache.size(), is(108));

        verifyZeroInteractions(cidadeService);

        cidadeService.flushCacheCidadesDistritos();
    }

    @Test
    @SneakyThrows
    public void getCidadesDistritos_deveRetornarOk_quandoInformarApenasDistritosComoV() {
        //Consulta sem cache
        var result = mvc.perform(get(URL + "/distritos")
                .param("apenasDistritos", "V")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        var map = new ObjectMapper().readValue(result, Map.class);

        assertThat(map.size(), is(63));

        verify(cidadeService)
            .getCidadesDistritos(Eboolean.V);

        //Consulta usando informações no cache
        var resultCache = mvc.perform(get(URL + "/distritos")
                .param("apenasDistritos", "V")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        var mapCache = new ObjectMapper().readValue(resultCache, Map.class);

        assertThat(mapCache.size(), is(63));

        verifyZeroInteractions(cidadeService);

        cidadeService.flushCacheCidadesDistritos();
    }

    @Test
    @SneakyThrows
    public void getCidadesDistritos_deveRetornarOk_quandoInformarApenasDistritosComoF() {
        //Consulta sem cache
        var result = mvc.perform(get(URL + "/distritos")
                .param("apenasDistritos", "F")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        var map = new ObjectMapper().readValue(result, Map.class);

        assertThat(map.size(), is(45));

        verify(cidadeService)
            .getCidadesDistritos(Eboolean.F);

        //Consulta usando informações no cache
        var resultCache = mvc.perform(get(URL + "/distritos")
                .param("apenasDistritos", "F")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        var mapCache = new ObjectMapper().readValue(resultCache, Map.class);

        assertThat(mapCache.size(), is(45));

        verifyZeroInteractions(cidadeService);

        cidadeService.flushCacheCidadesDistritos();
    }

    @Test
    @SneakyThrows
    public void limparCacheCidadesDistritos_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(delete(URL + "/distritos/limpar-cache"))
            .andExpect(status().isUnauthorized());

        verifyZeroInteractions(cidadeService);
    }

    @Test
    @SneakyThrows
    public void limparCacheCidadesDistritos_deveRetornarForbidden_quandoUsuarioAutenticadoNaoTiverPermissao() {
        mvc.perform(delete(URL + "/distritos/limpar-cache")
                .header("Authorization", getAccessToken(mvc, OPERACAO_ASSISTENTE)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$[*].message",
                containsInAnyOrder("Usuário sem permissão sobre a entidade requisitada.")));

        verifyZeroInteractions(cidadeService);
    }

    @Test
    @SneakyThrows
    public void limparCacheCidadesDistritos_deveRetornarOk_quandoLimparCacheDeCidadesDistritos() {
        mvc.perform(delete(URL + "/distritos/limpar-cache")
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());

        verify(cidadeService)
            .flushCacheCidadesDistritos();
    }
}
