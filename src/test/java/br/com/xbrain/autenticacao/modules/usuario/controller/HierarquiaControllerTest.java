package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.service.CanalService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHierarquiaAtivoService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(HierarquiaController.class)
@MockBeans({
    @MockBean(UsuarioSubCanalObserver.class),
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class)})
@Import(OAuth2ResourceConfig.class)
public class HierarquiaControllerTest {

    private static final String BASE_URL = "/api/usuarios-hierarquia";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CanalService canalService;
    @MockBean
    private UsuarioHierarquiaAtivoService usuarioHierarquiaAtivoService;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getCoordenadoresSubordinados_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mockMvc.perform(get(BASE_URL + "/coordenadores-subordinados"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getCoordenadoresSubordinados_deveRetornarOk_quandoUsuarioAutenticado() {
        when(canalService.usuarioHierarquia()).thenReturn(usuarioHierarquiaAtivoService);

        mockMvc.perform(get(BASE_URL + "/coordenadores-subordinados")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioHierarquiaAtivoService).validarCanal(any());
        verify(usuarioHierarquiaAtivoService).coordenadoresSubordinadosHierarquia(any());
        verify(canalService, times(2)).usuarioHierarquia();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getSupervisoresSubordinados_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mockMvc.perform(get(BASE_URL + "/supervisores-subordinados"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getSupervisoresSubordinados_deveRetornarOk_quandoUsuarioAutenticado() {
        when(canalService.usuarioHierarquia()).thenReturn(usuarioHierarquiaAtivoService);

        mockMvc.perform(get(BASE_URL + "/supervisores-subordinados")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioHierarquiaAtivoService).validarCanal(any());
        verify(usuarioHierarquiaAtivoService).supervisoresDaHierarquia(any());
        verify(canalService, times(2)).usuarioHierarquia();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getVendedoresSubordinados_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mockMvc.perform(get(BASE_URL + "/vendedores-subordinados"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getVendedoresSubordinados_deveRetornarOk_quandoUsuarioAutenticado() {
        when(canalService.usuarioHierarquia()).thenReturn(usuarioHierarquiaAtivoService);

        mockMvc.perform(get(BASE_URL + "/vendedores-subordinados")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioHierarquiaAtivoService).validarCanal(any());
        verify(usuarioHierarquiaAtivoService).vendedoresDaHierarquia(any());
        verify(canalService, times(2)).usuarioHierarquia();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getVendedoresDaHierarquiaDoSite_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mockMvc.perform(get(BASE_URL + "/vendedores-hierarquia-site"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getVendedoresDaHierarquiaDoSite_deveRetornarOk_quandoUsuarioAutenticado() {
        when(canalService.usuarioHierarquia()).thenReturn(usuarioHierarquiaAtivoService);

        mockMvc.perform(get(BASE_URL + "/vendedores-hierarquia-site")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioHierarquiaAtivoService).validarCanal(any());
        verify(usuarioHierarquiaAtivoService).vendedoresDaHierarquiaPorSite(any());
        verify(canalService, times(2)).usuarioHierarquia();
    }
}
