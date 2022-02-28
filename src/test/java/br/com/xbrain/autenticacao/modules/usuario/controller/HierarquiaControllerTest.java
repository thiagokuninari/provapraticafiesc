package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHierarquiaFiltros;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHierarquiaAtivoService;
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

import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class HierarquiaControllerTest {

    private static final String URL_API = "/api/usuarios-hierarquia";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private UsuarioHierarquiaAtivoService usuarioHierarquiaAtivoService;

    @Test
    @SneakyThrows
    public void getVendedoresDaHierarquiaDoSite_deveRetornarVendedores_quandoUsuarioLogadoAdmin() {
        var filtros = new UsuarioHierarquiaFiltros();
        filtros.setSiteId(5);

        when(autenticacaoService.getUsuarioCanal()).thenReturn(ECanal.ATIVO_PROPRIO);

        mockMvc.perform(get(URL_API + "/vendedores-hierarquia-site")
                .param("siteId", "5")
                .header("Authorization", getAccessToken(mockMvc, ADMIN))
                .header("X-Usuario-Canal", "ATIVO_PROPRIO")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioHierarquiaAtivoService, times(1)).vendedoresDaHierarquiaPorSite(eq(filtros));
    }

    @Test
    @SneakyThrows
    public void getVendedoresDaHierarquiaDoSite_deveRetornarVendedores_quandoUsuarioLogadoAssistenteOperacao() {
        var filtros = new UsuarioHierarquiaFiltros();
        filtros.setSiteId(5);
        filtros.setBuscarInativo(true);

        when(autenticacaoService.getUsuarioCanal()).thenReturn(ECanal.ATIVO_PROPRIO);

        mockMvc.perform(get(URL_API + "/vendedores-hierarquia-site")
                .param("siteId", "5")
                .header("Authorization", getAccessToken(mockMvc, OPERACAO_ASSISTENTE))
                .header("X-Usuario-Canal", "ATIVO_PROPRIO")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioHierarquiaAtivoService, times(1)).vendedoresDaHierarquiaPorSite(eq(filtros));
    }

    @Test
    @SneakyThrows
    public void getVendedoresDaHierarquiaDoSite_naoDeveRetornarVendedores_quandoUsuarioLogadoNaoPossuirPermissao() {
        var filtros = new UsuarioHierarquiaFiltros();
        filtros.setSiteId(5);

        when(autenticacaoService.getUsuarioCanal()).thenReturn(ECanal.AGENTE_AUTORIZADO);

        mockMvc.perform(get(URL_API + "/vendedores-hierarquia-site")
                .param("siteId", "5")
                .header("Authorization", getAccessToken(mockMvc, SOCIO_AA))
                .header("X-Usuario-Canal", "AGENTE_AUTORIZADO")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(usuarioHierarquiaAtivoService, times(0)).vendedoresDaHierarquiaPorSite(eq(filtros));
    }
}
