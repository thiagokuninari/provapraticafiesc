package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.service.SubClusterService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.exceptions.SubCanalCustomExceptionHandler;
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

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(SubClusterController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
    @MockBean(SubCanalCustomExceptionHandler.class),
})
@Import(OAuth2ResourceConfig.class)
public class SubClusterControllerTest {

    private static String API_SUBCLUSTER = "/api/subclusters";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private SubClusterService subClusterService;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAtivosPorCluster_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_SUBCLUSTER)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAtivosPorCluster_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(API_SUBCLUSTER + "?clusterId=16")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(subClusterService).getAllByClusterId(16);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAtivosPorCluster_deveRetornarOk_seNaoInformarClusterId() {
        mvc.perform(get(API_SUBCLUSTER)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(subClusterService).getAllAtivos();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllByClusterIdAndUsuarioId_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_SUBCLUSTER + "/cluster/{clusterId}/usuario/{usuarioId}", 4, 100)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllByClusterIdAndUsuarioId_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(API_SUBCLUSTER + "/cluster/{clusterId}/usuario/{usuarioId}", 4, 100)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(subClusterService).getAllByClusterIdAndUsuarioId(4, 100);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getById_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_SUBCLUSTER + "/45"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getById_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(API_SUBCLUSTER + "/45")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(subClusterService).getById(45);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAtivosPorClusters_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_SUBCLUSTER + "/clusters")
            .param("clustersId", "10", "20", "30"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAtivosPorClusters_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(API_SUBCLUSTER + "/clusters")
                .param("clustersId", "10", "20", "30"))
            .andExpect(status().isOk());

        verify(subClusterService).getAllByClustersId(List.of(10, 20, 30));
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAtivosPorClusters_deveRetornarOk_seNaoInformarClustersId() {
        mvc.perform(get(API_SUBCLUSTER + "/clusters"))
            .andExpect(status().isOk());

        verify(subClusterService).getAllAtivos();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAtivosParaComunicados_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_SUBCLUSTER + "/comunicados")
                .param("clusterId", "10"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAtivosParaComunicados_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(API_SUBCLUSTER + "/comunicados")
                .param("clusterId", "10"))
            .andExpect(status().isOk());

        verify(subClusterService).getAtivosParaComunicados(10);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllSubclusters_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_SUBCLUSTER + "/todos"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllSubclusters_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(API_SUBCLUSTER + "/todos"))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllSubClustersDoUsuarioAutenticado_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(API_SUBCLUSTER + "/usuario-autenticado")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllSubClustersDoUsuarioAutenticado_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(API_SUBCLUSTER + "/usuario-autenticado")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(subClusterService).getAllSubclustersByUsuarioAutenticado();
    }
}
