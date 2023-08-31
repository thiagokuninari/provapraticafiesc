package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadesUfsRequest;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
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
    public void getByIdGrupo_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/grupo/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getByIdGrupo_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL + "/grupo/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).getAllByGrupoId(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getByIdCluster_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/cluster/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getByIdCluster_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL + "/cluster/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).getAllByClusterId(1);
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
    public void getAtivosParaComunicados_deveRetornarOk_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/comunicados")
                .accept(MediaType.APPLICATION_JSON)
                .param("subclusterId", "1"))
            .andExpect(status().isOk());

        verify(cidadeService).getAtivosParaComunicados(1);
    }

    public void deveRetornarTodosPorUf() throws Exception {
        mvc.perform(get("/api/cidades?idUf=1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(8)));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getByIdSubClusters_deveRetornarOk_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/sub-clusters")
                .param("subclustersId", "1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
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
    public void getCidadeById_deveRetornarOk_quandoUsuarioNaoAutenticado() {
        when(cidadeService.findById(1)).thenReturn(umaCidade());

        mvc.perform(get(BASE_URL + "/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(cidadeService).findById(1);
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
        mvc.perform(post("/api/cidades/por-nome-e-ufs")
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
