package br.com.xbrain.autenticacao.modules.autenticacao.controller;

import br.com.xbrain.autenticacao.config.CustomTokenEndpointAuthenticationFilter;
import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHistoricoDto;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHistoricoRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioHistoricoService;
import helpers.OAuthToken;
import helpers.TestsHelper;
import helpers.Usuarios;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class AutenticacaoControllerTest {

    private static final int USUARIO_SOCIO_ID = 226;

    @Autowired
    private MockMvc mvc;
    @MockBean
    private AgenteAutorizadoService agenteAutorizadoService;
    @Autowired
    private UsuarioHistoricoService usuarioHistoricoService;
    @Autowired
    private UsuarioHistoricoRepository usuarioHistoricoRepository;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private TokenStore tokenStore;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @Autowired
    private CustomTokenEndpointAuthenticationFilter customTokenEndpointAuthenticationFilter;

    @Test
    public void checkToken_ok_quandoForUmaTokenValida() throws Exception {
        OAuthToken token = TestsHelper.getAccessTokenObject(mvc, Usuarios.ADMIN);

        mvc.perform(
                post("/oauth/check_token")
                    .param("token", token.getAccessToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.usuarioId", is(100)))
            .andExpect(jsonPath("$.nome", is("ADMIN")))
            .andExpect(jsonPath("$.email", is("ADMIN@XBRAIN.COM.BR")))
            .andExpect(jsonPath("$.login", is("100-ADMIN@XBRAIN.COM.BR")))
            .andExpect(jsonPath("$.nivel", is("X-BRAIN")))
            .andExpect(jsonPath("$.departamento", is("Administrador")))
            .andExpect(jsonPath("$.cargo", is("Administrador")))
            .andExpect(jsonPath("$.cargoCodigo", is("ADMINISTRADOR")))
            .andExpect(jsonPath("$.departamentoCodigo", is("ADMINISTRADOR")))
            .andExpect(jsonPath("$.nivelCodigo", is("XBRAIN")))
            .andExpect(jsonPath("$.empresas", is(Collections.singletonList(4))))
            .andExpect(jsonPath("$.empresasNome", is(Collections.singletonList("Xbrain"))))
            .andExpect(jsonPath("$.empresasCodigo", is(Collections.singletonList("XBRAIN"))))
            .andExpect(jsonPath("$.authorities", not(empty())))
            .andExpect(jsonPath("$.aplicacoes", not(empty())))
            .andExpect(jsonPath("$.cpf", is("38957979875")))
            .andExpect(jsonPath("$.agentesAutorizados", is(empty())))
            .andExpect(jsonPath("$.equipesSupervisionadas", is(empty())))
            .andExpect(jsonPath("$.loginNetSales", is("123456")))
            .andExpect(jsonPath("$.nomeEquipeVendaNetSales", is("VENDAS NET")))
            .andExpect(jsonPath("$.codigoEquipeVendaNetSales", is("654321")))
            .andExpect(jsonPath("$.canalNetSales", is("D2D_ACOES_ESPECIAIS")));
    }

    @Test
    public void getAccessToken_deveAutenticar_quandoAsCredenciaisEstiveremValidas() {
        OAuthToken token = TestsHelper.getAccessTokenObject(mvc, Usuarios.ADMIN);
        assertNotNull(token.getAccessToken());
        assertEquals("100-ADMIN@XBRAIN.COM.BR", token.getLogin());
        assertEquals("100", token.getUsuarioId());
        assertEquals("ADMIN@XBRAIN.COM.BR", token.getEmail());
        assertEquals("ADMIN", token.getNome());
        assertEquals("X-BRAIN", token.getNivel());
        assertEquals("Administrador", token.getDepartamento());
        assertEquals("Administrador", token.getCargo());
        assertEquals("XBRAIN", token.getNivelCodigo());
        assertEquals("ADMINISTRADOR", token.getCargoCodigo());
        assertEquals("ADMINISTRADOR", token.getDepartamentoCodigo());
        assertEquals("F", token.getAlterarSenha());
        assertEquals("38957979875", token.getCpf());
        assertFalse(token.getAuthorities().isEmpty());
        assertFalse(token.getAplicacoes().isEmpty());
        assertEquals(singletonList(4), token.getEmpresas());
        assertEquals(singletonList(3), token.getUnidadesNegocios());
    }

    @Test
    public void getAccessToken_deveNaoAutenticar_quandoAsCredenciasEstiveremInvalidas() {
        OAuthToken token = TestsHelper.getAccessTokenObject(mvc, "INVALIDO@XBRAIN.COM.BR");
        assertNull(token.getAccessToken());
    }

    @Test
    public void getAccessToken_deveIncluirOsAAsPermitidos_quandoForNivelAgenteAutorizado() throws Exception {
        when(agenteAutorizadoService.getAasPermitidos(USUARIO_SOCIO_ID)).thenReturn(Arrays.asList(1, 2));

        OAuthToken token = TestsHelper.getAccessTokenObject(mvc, Usuarios.SOCIO_AA);

        mvc.perform(
                post("/oauth/check_token")
                    .param("token", token.getAccessToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.usuarioId", is(USUARIO_SOCIO_ID)))
            .andExpect(jsonPath("$.nivelCodigo", is("AGENTE_AUTORIZADO")))
            .andExpect(jsonPath("$.agentesAutorizados", is(Arrays.asList(1, 2))));
    }

    @Test
    public void getAccessToken_deveIncluirAsEmpresasDoAa_quandoForNivelAgenteAutorizado() throws Exception {
        when(agenteAutorizadoService.getEmpresasPermitidas(USUARIO_SOCIO_ID))
            .thenReturn(Arrays.asList(
                new Empresa(1, "CLARO MOVEL", CodigoEmpresa.CLARO_MOVEL),
                new Empresa(2, "NET", CodigoEmpresa.NET)
            ));

        OAuthToken token = TestsHelper.getAccessTokenObject(mvc, Usuarios.SOCIO_AA);

        mvc.perform(
                post("/oauth/check_token")
                    .param("token", token.getAccessToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.usuarioId", is(USUARIO_SOCIO_ID)))
            .andExpect(jsonPath("$.empresas", is(Arrays.asList(1, 2))))
            .andExpect(jsonPath("$.empresasNome", is(Arrays.asList("CLARO MOVEL", "NET"))))
            .andExpect(jsonPath("$.empresasCodigo", is(Arrays.asList("CLARO_MOVEL", "NET"))));
    }

    @Test
    public void getAccessToken_deveRetornarPmeParaAa_quandoForAgenteAutorizadoPme() throws Exception {
        when(agenteAutorizadoService.getEstruturaByUsuarioIdAndAtivo(USUARIO_SOCIO_ID))
            .thenReturn("AA_PME");

        OAuthToken token = TestsHelper.getAccessTokenObject(mvc, Usuarios.SOCIO_AA);

        mvc.perform(
                post("/oauth/check_token")
                    .param("token", token.getAccessToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.usuarioId", is(USUARIO_SOCIO_ID)))
            .andExpect(jsonPath("$.estruturaAa", is("AA_PME")));
    }

    @Test
    public void getAccessToken_badRequest_quandoATokenForInvalida() throws Exception {
        mvc.perform(
                post("/oauth/check_token")
                    .param("token", "teste"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void getAccessTokenClientCredentials_ok_quandoAsCredenciaisEstiveremValidas() {
        assertNotNull(TestsHelper
            .getAccessTokenClientCredentials(mvc, "parceiros-online-api:p4rc31r0s$p1").getAccessToken());

        assertNotNull(TestsHelper
            .getAccessTokenClientCredentials(mvc, "vendas-api:v3nd4s4p1").getAccessToken());

        assertNotNull(TestsHelper
            .getAccessTokenClientCredentials(mvc, "mailing-api:m41l1ng4p1").getAccessToken());

        assertNotNull(TestsHelper
            .getAccessTokenClientCredentials(mvc, "equipe-venda-api:3qu1p3V3nD4xbr41n").getAccessToken());
    }

    @Test
    public void getAccessTokenClientCredentials_null_quandoAsCredenciaisEstiveremInvalidas() {
        OAuthToken token = TestsHelper.getAccessTokenClientCredentials(mvc, "parceiros-online-api:invalida");
        assertNull(token.getAccessToken());
    }

    @Test
    public void getTokenResponse_401_quandoOUsuarioEstiverInativo() throws Exception {
        MockHttpServletResponse response = TestsHelper.getTokenResponse(mvc, Usuarios.INATIVO);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains(
            "Usu&aacute;rio Inativo, solicite a ativa&ccedil;&atilde;o ao seu respons&aacute;vel."));
    }

    @Test
    public void getTokenResponse_401_quandoOUsuarioEstiverPendente() throws Exception {
        MockHttpServletResponse response = TestsHelper.getTokenResponse(mvc, Usuarios.PENDENTE);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains(
            "Agente Autorizado com aceite de contrato pendente."));
    }

    // TODO foi desativado e será refeito conforme task #13110
    //@Test
    public void getAccessTokenObject_deveGerarHistorico_quandoAutenticar() {
        TestsHelper.getAccessTokenObject(mvc, Usuarios.ADMIN);

        List<UsuarioHistoricoDto> historico = usuarioHistoricoService.getHistoricoDoUsuario(101);
        assertTrue(!historico.isEmpty());
        assertEquals(1, historico.stream().filter(h -> "ÚLTIMO ACESSO DO USUÁRIO".equals(h.getSituacao())).count());
    }

    @Test
    public void getAccessTokenObject_deveNaoGerarHistorico_quandoNaoAutenticar() {
        long totalRegistrosAntes = usuarioHistoricoRepository.findAll().spliterator().getExactSizeIfKnown();

        TestsHelper.getAccessTokenObject(mvc, "INVALIDO@XBRAIN.COM.BR");

        long totalRegistroApos = usuarioHistoricoRepository.findAll().spliterator().getExactSizeIfKnown();
        assertTrue(totalRegistrosAntes == totalRegistroApos);
    }

    @Test
    @SuppressWarnings("VariableDeclarationUsageDistance")
    public void deveValidarPausa_void_quandoPossuirUsernameRequest() throws IOException, ServletException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(equipeVendaD2dService.verificaPausaEmAndamento(anyString())).thenReturn(Boolean.TRUE);
        when(req.getRequestURI()).thenReturn("/oauth/token");
        when(req.getContentType()).thenReturn("multipart/form-data");
        when(req.getParameter(eq("username"))).thenReturn("MATEUS@XBRAIN.COM.BR");

        customTokenEndpointAuthenticationFilter.doFilter(req, res, chain);

        verify(equipeVendaD2dService, times(1)).verificaPausaEmAndamento(eq("MATEUS@XBRAIN.COM.BR"));
    }

    @Test
    @SuppressWarnings("VariableDeclarationUsageDistance")
    public void naoDeveValidarPausa_void_quandoNaoPossuirUsernameRequest() throws IOException, ServletException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(equipeVendaD2dService.verificaPausaEmAndamento(anyString())).thenReturn(Boolean.TRUE);
        when(req.getRequestURI()).thenReturn("/oauth/token");
        when(req.getContentType()).thenReturn("multipart/form-data");
        when(req.getParameter(eq("username"))).thenReturn(null);

        customTokenEndpointAuthenticationFilter.doFilter(req, res, chain);

        verify(equipeVendaD2dService, times(0)).verificaPausaEmAndamento(any());
    }
}
