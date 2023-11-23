package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.service.ClusterService;
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

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(ClusterController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
    @MockBean(SubCanalCustomExceptionHandler.class),
})
@Import(OAuth2ResourceConfig.class)
public class ClusterControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ClusterService clusterService;

    @Test
    @SneakyThrows
    public void getAtivosPorGrupo_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get("/api/clusters")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAtivosPorGrupo_deveRetornarOk_seInformarGrupoId() {
        mvc.perform(get("/api/clusters?grupoId=30")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(clusterService).getAllByGrupoId(30);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAtivosPorGrupo_deveRetornarOK_seNaoInformarGrupoId() {
        mvc.perform(get("/api/clusters")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(clusterService).getAllAtivo();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAtivosParaComunicados_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get("/api/clusters/comunicados?grupoId=30")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAtivosParaComunicados_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get("/api/clusters/comunicados?grupoId=30")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(clusterService).getAtivosParaComunicados(30);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAtivosParaComunicados_deveRetornarBadRequest_seNaoInformarGrupoId() {
        mvc.perform(get("/api/clusters/comunicados")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllByGrupoIdAndUsuarioId_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get("/api/clusters/grupo/{grupoId}/usuario/{usuarioId}", 30, 100)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllByGrupoIdAndUsuarioId_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get("/api/clusters/grupo/{grupoId}/usuario/{usuarioId}", 30, 100)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(clusterService).getAllByGrupoIdAndUsuarioId(30, 100);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findById_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get("/api/clusters/{clusterId}", 10)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findById_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get("/api/clusters/{clusterId}", 10)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(clusterService).findById(10);
    }
}
