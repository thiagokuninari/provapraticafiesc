package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadesUfsRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper.cidadeResponseLondrina;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper.cidadeResponsePolvilhoComCidadePai;
import static helpers.TestsHelper.*;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(CidadeController.class)
@MockBeans({
    @MockBean(UsuarioSubCanalObserver.class),
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class)
})
@Import(OAuth2ResourceConfig.class)
public class CidadeControllerTest {

    private static final String BASE_URL = "/api/cidades";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private CidadeService cidadeService;
    @MockBean
    private AutenticacaoService autenticacaoService;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarTodas_deveRetornarOk_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL)
                .param("idUf", "1")
                .param("idRegional", "2")
                .param("idSubCluster", "3")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).buscarTodas(1, 2, 3);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getByUfAndNome_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/uf-cidade/PR/LONDRINA")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getByUfAndNome_deveRetornarOk_quandoUsuarioAutenticado() {
        when(cidadeService.findByUfNomeAndCidadeNome("PR", "LONDRINA"))
            .thenReturn(umaCidade());

        mvc.perform(get(BASE_URL + "/uf-cidade/PR/LONDRINA")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).findByUfNomeAndCidadeNome("PR", "LONDRINA");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getCidadeSiteByUfAndNome_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/PR/LONDRINA/site")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getCidadeSiteByUfAndNome_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL + "/PR/LONDRINA/site")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).findCidadeComSiteByUfECidade("PR", "LONDRINA");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getCidadeSubcluster_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "recuperar-cidade/PR/LONDRINA")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getCidadeSubcluster_deveRetornarOk_quandoUsuarioAutenticado() {
        when(cidadeService.findByUfNomeAndCidadeNome("PR", "LONDRINA"))
            .thenReturn(umaCidade());

        mvc.perform(get(BASE_URL + "/recuperar-cidade/PR/LONDRINA")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).findByUfNomeAndCidadeNome("PR", "LONDRINA");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getByIdRegional_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/regional/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getByIdRegional_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL + "/regional/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).getAllByRegionalId(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getByIdRegionalAndIdUf_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/regional/1/uf/2")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getByIdRegionalAndIdUf_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL + "/regional/1/uf/2")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).getAllByRegionalIdAndUfId(1, 2);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getByIdSubCluster_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/sub-cluster/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getByIdSubCluster_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL + "/sub-cluster/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).getAllBySubClusterId(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getById_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/cidade/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getById_deveRetornarOk_quandoUsuarioAutenticado() {
        when(cidadeService.findById(1)).thenReturn(umaCidade());

        mvc.perform(get(BASE_URL + "/cidade/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).findById(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getCidadeById_deveRetornarBadRequest_quandoNaoEncontrarCidadePorId() {
        doThrow(new ValidacaoException("Cidade não encontrada."))
            .when(cidadeService)
            .getCidadeById(15000);

        mvc.perform(get(BASE_URL + "/{cidadeId}", 15000)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder("Cidade não encontrada.")));

        verify(cidadeService).getCidadeById(15000);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getCidadeById_deveRetornarOk_quandoEncontrarCidadePorIdSemNomeCidadePai() {
        when(cidadeService.getCidadeById(5578)).thenReturn(cidadeResponseLondrina());

        mvc.perform(get(BASE_URL + "/{cidadeId}", 5578)
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

        verify(cidadeService).getCidadeById(5578);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getCidadeById_deveRetornarOk_quandoEncontrarCidadePorIdComNomeCidadePai() {
        when(cidadeService.getCidadeById(33302)).thenReturn(cidadeResponsePolvilhoComCidadePai());

        mvc.perform(get(BASE_URL + "/{cidadeId}", 33302)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(33302)))
            .andExpect(jsonPath("$.nome", is("POLVILHO")))
            .andExpect(jsonPath("$.uf.id", is(2)))
            .andExpect(jsonPath("$.uf.nome", is("SAO PAULO")))
            .andExpect(jsonPath("$.regional.id", is(1031)))
            .andExpect(jsonPath("$.regional.nome", is("RSI")))
            .andExpect(jsonPath("$.fkCidade", is(4903)))
            .andExpect(jsonPath("$.cidadePai", is("CAJAMAR")));

        verify(cidadeService).getCidadeById(33302);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAll_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/1/clusterizacao")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAll_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL + "/1/clusterizacao")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).getClusterizacao(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllCidadeNetUno_deveRetornarOk_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/net-uno")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).getAllCidadeNetUno();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarCidadesPorEstados_deveRetornarOk_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/por-estados")
                .param("estadosIds", "1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).buscarCidadesPorEstadosIds(List.of(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarCidadesPorRegionalParaReprocessamento_deveRetornarOk_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/reprocessamento-regional")
                .param("regionalId", "1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).getCidadesByRegionalReprocessamento(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarCidadesPorRegionalAndUfParaReprocessamento_deveRetornarOk_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/reprocessamento-uf")
                .param("regionalId", "1")
                .param("ufId", "2")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).getCidadesByRegionalAndUfReprocessamento(1, 2);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getCidadeByCodigoCidadeDbm_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/cidade-dbm/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getCidadeByCodigoCidadeDbm_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL + "/cidade-dbm/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).getCidadeByCodigoCidadeDbm(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllCidadeByUfs_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/")
                .param("ufIds", "1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllCidadeByUfs_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL + "/")
                .param("ufIds", "1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).getAllCidadeByUfs(List.of(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarCidadeUfIds_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/uf-cidade-ids/PR/LONDRINA")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void buscarCidadeUfIds_deveRetornarOk_quandoUsuarioAutenticado() {
        when(cidadeService.findByUfNomeAndCidadeNome("PR", "LONDRINA"))
            .thenReturn(umaCidade());

        mvc.perform(get(BASE_URL + "/uf-cidade-ids/PR/LONDRINA")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).findByUfNomeAndCidadeNome("PR", "LONDRINA");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findCidadeByCodigoIbge_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/codigo-ibge/codigo")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findCidadeByCodigoIbge_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL + "/codigo-ibge/codigo")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).findCidadeByCodigoIbge("codigo");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findCidadesByCodigosIbge_deveRetornarOk_quandoUsuarioNaoAutenticado() {
        mvc.perform(post(BASE_URL + "/codigos-ibge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(List.of("4113700", "3527108")))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarCidadeEstadoIds_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/estado-cidade-ids/PR/LONDRINA")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void buscarCidadeEstadoIds_deveRetornarOk_quandoUsuarioAutenticado() {
        when(cidadeService.findFirstByUfNomeAndCidadeNome("PR", "LONDRINA"))
            .thenReturn(umaCidade());

        mvc.perform(get(BASE_URL + "/estado-cidade-ids/PR/LONDRINA")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).findFirstByUfNomeAndCidadeNome("PR", "LONDRINA");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getCodigoIbgeRegionalByCidade_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/codigo-ibge/regional")
                .param("cidadesId", "1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getCodigoIbgeRegionalByCidade_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL + "/codigo-ibge/regional")
                .param("cidadesId", "1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).getCodigoIbgeRegionalByCidade(List.of(1));
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
        mvc.perform(post(BASE_URL + "/por-nome-e-ufs")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(cidadeService, never()).getCodigoIbgeRegionalByCidadeNomeAndUf(any(CidadesUfsRequest.class));
    }

    @Test
    @SneakyThrows
    public void getCodigoIbgeRegionalByCidadeNomeAndUf_deveRetornarZero_quandoInformarListasVaziasDeCidadesEUfs() {
        mvc.perform(post("/api/cidades/por-nome-e-ufs")
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(listasCidadesUfsInvalida()))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());

        verify(cidadeService, times(1))
            .getCodigoIbgeRegionalByCidadeNomeAndUf(eq(listasCidadesUfsInvalida()));
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getCodigoIbgeRegionalByCidadeNomeAndUf_deveRetornarListaCodigoIbgeRegionalResponse_quandoListasForemValidas() {
        mvc.perform(post("/api/cidades/por-nome-e-ufs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(listasCidadesUfsValida())))
            .andExpect(status().isOk());

        verify(cidadeService, times(1))
            .getCodigoIbgeRegionalByCidadeNomeAndUf(any(CidadesUfsRequest.class));
    }

    @Test
    @SneakyThrows
    public void getAllCidades_deveRetornarOk_quandoNaoInformarParametros() {
        mvc.perform(get(BASE_URL + "/todas")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService)
            .getAll(null, null);
    }

    @Test
    @SneakyThrows
    public void getAllCidades_deveRetornarOk_quandoInformarApenasRegionalId() {
        mvc.perform(get(BASE_URL + "/todas")
                .param("regionalId", "1030")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService)
            .getAll(1030, null);
    }

    @Test
    @SneakyThrows
    public void getAllCidades_deveRetornarOk_quandoInformarApenasUfId() {
        mvc.perform(get(BASE_URL + "/todas")
                .param("ufId", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService)
            .getAll(null, 2);
    }

    @Test
    @SneakyThrows
    public void getAllCidades_deveRetornarOk_quandoInformarRegionalIdAndUfId() {
        mvc.perform(get(BASE_URL + "/todas")
                .param("regionalId", "1031")
                .param("ufId", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService)
            .getAll(1031, 2);
    }

    @Test
    @SneakyThrows
    public void getCidadeById_deveRetornarOk_quandoEncontrarCidade() {
        mvc.perform(get(BASE_URL + "/{cidadeId}", 5578)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService)
            .getCidadeById(5578);
    }

    @Test
    @SneakyThrows
    public void getCidadesDistritos_deveRetornarOk_quandoNaoInformarParametro() {
        mvc.perform(get(BASE_URL + "/distritos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService)
            .getCidadesDistritos(null);
    }

    @Test
    @SneakyThrows
    public void getCidadesDistritos_deveRetornarOk_quandoInformarApenasDistritos() {
        mvc.perform(get(BASE_URL + "/distritos")
                .param("apenasDistritos", "V")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService)
            .getCidadesDistritos(Eboolean.V);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void limparCacheCidadesDistritos_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(delete(BASE_URL + "/distritos/limpar-cache"))
            .andExpect(status().isUnauthorized());

        verifyZeroInteractions(cidadeService);
        verifyZeroInteractions(autenticacaoService);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void limparCacheCidadesDistritos_deveRetornarForbidden_quandoUsuarioAutenticadoNaoTiverPermissao() {
        var usuarioAutenticado = new UsuarioAutenticado();
        usuarioAutenticado.setNivelCodigo(CodigoNivel.OPERACAO.name());

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioAutenticado);

        mvc.perform(delete(BASE_URL + "/distritos/limpar-cache")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$[*].message",
                containsInAnyOrder("Usuário sem permissão sobre a entidade requisitada.")));

        verifyZeroInteractions(cidadeService);
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void limparCacheCidadesDistritos_deveRetornarOk_quandoLimparCacheDeCidadesDistritos() {
        var usuarioAutenticado = new UsuarioAutenticado();
        usuarioAutenticado.setNivelCodigo(CodigoNivel.XBRAIN.name());

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioAutenticado);

        mvc.perform(delete(BASE_URL + "/distritos/limpar-cache")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService)
            .flushCacheCidadesDistritos();
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getCidadeDistrito_deveRetornarOk_quandoInformarParametrosCorretos() {
        mvc.perform(get(BASE_URL + "/cidade-distrito")
                .param("uf", "PR")
                .param("cidade", "LONDRINA")
                .param("distrito", "SAO LUIZ")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).getCidadeDistrito(eq("PR"), eq("LONDRINA"), eq("SAO LUIZ"));
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getCidadesByCidadeInstalacaoIds_deveRetornarOk_quandoInformarParametrosCorretos() {
        mvc.perform(post(BASE_URL + "/cidade-instalacao-ids")
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(List.of(5578))))
            .andExpect(status().isOk());

        verify(cidadeService)
            .getCidadesByCidadeInstalacaoIds(eq(List.of(5578)));
    }

    private Cidade umaCidade() {
        return Cidade.builder()
            .id(5578)
            .nome("LONDRINA")
            .subCluster(SubCluster.builder().id(189).build())
            .uf(Uf.builder().id(1).nome("PARANA").build())
            .regional(new Regional(1))
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
