package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.permissao.service.JsonWebTokenService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioExecutivoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioPermissoesResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSituacaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.repository.ConfiguracaoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioAgendamentoService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import helpers.Usuarios;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.usuariosMesmoSegmentoAgenteAutorizado1300;
import static helpers.TestBuilders.*;
import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class UsuarioControllerTest {
    private static final String URL_USUARIOS_AGENDAMENTOS = "/api/usuarios/distribuicao/agendamentos/";
    private static final String USUARIOS_ENDPOINT = "/api/usuarios";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private ConfiguracaoRepository configuracaoRepository;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private EmailService emailService;
    @Autowired
    private JsonWebTokenService jsonWebTokenService;
    @SpyBean
    private UsuarioService usuarioService;
    @MockBean
    private UsuarioAgendamentoService usuarioAgendamentoService;

    @Before
    public void setup() {
        when(autenticacaoService.getUsuarioId())
            .thenReturn(100);
        when(usuarioAgendamentoService.recuperarUsuariosParaDistribuicao(eq(131), eq(1300)))
                .thenReturn(usuariosMesmoSegmentoAgenteAutorizado1300());
    }

    @Test
    public void deveSolicitarAutenticacao() throws Exception {
        mvc.perform(get("/api/usuarios")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/usuarios/101")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void deveRetornarTodasAsCidadesDoUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/100/cidades")
            .header("Authorization", getAccessToken(mvc, Usuarios.HELP_DESK))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].nome", is("LONDRINA")));
    }

    @Test
    public void deveRetornarNenhumaCidadeParaOUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/101/cidades")
            .header("Authorization", getAccessToken(mvc, Usuarios.HELP_DESK))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void deveRetornarUsuariosPorIds() throws Exception {
        mvc.perform(get("/api/usuarios?ids=100,101,104")
            .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void deveNaoRetornarUsuariosPorIdsInativos() throws Exception {
        mvc.perform(get("/api/usuarios?ids=105")
            .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void deveRetornarUsuarioPorIdEVerificarPermissoes() throws Exception {
        mvc.perform(get("/api/usuarios/100")
            .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(100)))
            .andExpect(jsonPath("$.nome", is("ADMIN")))
            .andExpect(jsonPath("$.email", is("ADMIN@XBRAIN.COM.BR")))
            .andExpect(jsonPath("$.permissoes[0]", is("ROLE_AUT_ATUALIZAR_SENHA_REENVIAR_EMAIL")));
    }

    @Test
    public void deveRetornarUsuarioAutenticadoPorId() throws Exception {
        mvc.perform(get("/api/usuarios/autenticado/101")
            .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(101)))
            .andExpect(jsonPath("$.nome", is("HELPDESK")))
            .andExpect(jsonPath("$.email", is("HELPDESK@XBRAIN.COM.BR")));
    }

    @Test
    public void deveRetornarOUsuarioPorEmail() throws Exception {
        mvc.perform(get("/api/usuarios?email=ADMIN@XBRAIN.COM.BR")
            .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(100)))
            .andExpect(jsonPath("$.nome", is("ADMIN")))
            .andExpect(jsonPath("$.email", is(Usuarios.ADMIN)));
    }

    @Test
    public void deveRetornarUsuarioPorCpf() throws Exception {
        mvc.perform(get("/api/usuarios?cpf=65710871036")
            .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(101)))
            .andExpect(jsonPath("$.nome", is("HELPDESK")))
            .andExpect(jsonPath("$.email", is(Usuarios.HELP_DESK)));
    }

    @Test
    public void deveRetornarAsEmpresasDoUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/100/empresas")
            .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void deveRetornarUsuariosPorNivelDoCargo() throws Exception {
        mvc.perform(get("/api/usuarios?nivel=XBRAIN")
            .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    public void deveRetornarUsuariosPorPermissaoEspecial() throws Exception {
        mvc.perform(get("/api/usuarios?funcionalidade=POL_AGENTE_AUTORIZADO_APROVACAO_MSO_NOVO_CADASTRO")
            .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void deveRetornarAsConfiguracoesDoUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/configuracao")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ramal", is(7006)));
    }

    @Test
    public void deveAdicionarConfiguracaoAoUsuario() throws Exception {
        mvc.perform(post("/api/usuarios/adicionar-configuracao")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umUsuarioConfiguracaoDto())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ramal", is(1000)));
    }

    @Test
    public void deveRemoverConfiguracaoAoUsuario() throws Exception {
        long quantidadeAntes = configuracaoRepository.count();

        mvc.perform(put("/api/usuarios/remover-configuracao")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umUsuarioConfiguracaoDto())))
            .andExpect(status().isOk());

        long quantidadeDepois = configuracaoRepository.count();

        Assert.assertTrue(quantidadeAntes > quantidadeDepois);
    }

    @Test
    public void deveRemoverRamalConfiguracaoAoUsuario() throws Exception {
        var dto = umaListDeUsuarioConfiguracaoDto();
        Assert.assertFalse(configuracaoRepository.findByRamal(dto.get(0).getRamal()).isEmpty());

        mvc.perform(put("/api/usuarios/remover-ramais-configuracao")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(dto)))
            .andExpect(status().isOk());

        Assert.assertTrue(configuracaoRepository.findByRamal(dto.get(0).getRamal()).isEmpty());
    }

    @Test
    public void deveEnviarConfirmacaoDeResetDeSenhaEmail() throws Exception {
        mvc.perform(put("/api/usuarios/esqueci-senha")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umEsqueciSenha())))
            .andExpect(status().isOk());
        verify(emailService, times(1)).enviarEmailTemplate(any(), any(), any(), any());
    }

    @Test
    public void deveNaoResetarSenhaDoUsuarioComToken() throws Exception {
        mvc.perform(put("/api/usuarios/esqueci-senha")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void deveResetarSenhaDoUsuarioComToken() throws Exception {
        String hash = jsonWebTokenService.createJsonWebTokenResetSenha("teste@xbrain.com.br", 2);

        mvc.perform(put("/api/usuarios/esqueci-senha?hash=" + hash)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umEsqueciSenha())))
            .andExpect(status().isOk());
        verify(emailService, times(1)).enviarEmailTemplate(any(), any(), any(), any());
    }

    @Test
    public void getPermissoesPorCanal_permissoesComCanal_somentePermitidasAoUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/permissoes-por-canal")
            .header("Authorization", getAccessToken(mvc, SOCIO_AA))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4)))
            .andExpect(jsonPath("$[0].permissao", is("ROLE_VDS_3021")))
            .andExpect(jsonPath("$[0].canais", hasSize(2)))
            .andExpect(jsonPath("$[0].canais[0]", is("AGENTE_AUTORIZADO")))
            .andExpect(jsonPath("$[0].canais[1]", is("ATIVO")));
    }

    @Test
    public void getSubclustersUsuario_deveRetornarOsSubclusters_conformeUsuarioIdInformado() throws Exception {
        mvc.perform(get("/api/usuarios/100/subclusters")
            .header("Authorization", getAccessToken(mvc, SOCIO_AA))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].value", is(189)))
            .andExpect(jsonPath("$[0].label", is("LONDRINA - Claro")));

    }

    @Test
    public void getPermissoesPorUsuarios_throwException_QuandoParametrosVazios() throws Exception {
        mvc.perform(get("/api/usuarios/permissoes-por-usuario")
            .header("Authorization", getAccessToken(mvc, SOCIO_AA))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo permissoes é obrigatório.",
                "O campo usuariosId é obrigatório.")));
    }

    @Test
    public void getPermissoesPorUsuarios_usuarioComPermissoes_QuandoFornecidoParametros() throws Exception {
        doReturn(Collections.singletonList(
            new UsuarioPermissoesResponse(
                2844,
                Collections.singletonList("ROLE_VDS_TABULACAO_CLICKTOCALL"))))
            .when(usuarioService).findUsuariosByPermissoes(any());
        mvc.perform(get("/api/usuarios/permissoes-por-usuario?usuariosId=2844&permissoes=ROLE_VDS_TABULACAO_CLICKTOCALL")
            .header("Authorization", getAccessToken(mvc, SOCIO_AA))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].usuarioId", is(2844)))
            .andExpect(jsonPath("$[0].permissoes", containsInAnyOrder("ROLE_VDS_TABULACAO_CLICKTOCALL")));
    }

    @Test
    public void getSuperioresByUsuario_deveRetornar_quandoForValido() throws Exception {
        doReturn(Collections.singletonList(umUsuarioHierarquia()))
            .when(usuarioService).getSuperioresDoUsuario(anyInt());

        mvc.perform(get("/api/usuarios/hierarquia/superiores/1")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(100)))
            .andExpect(jsonPath("$[0].nome", is("XBRAIN")));
    }

    @Test
    public void getSuperioresByUsuario_deveRetornarNada_quandoNaoPossuirSuperior() throws Exception {
        doReturn(Collections.emptyList())
            .when(usuarioService).getSuperioresDoUsuario(anyInt());

        mvc.perform(get("/api/usuarios/hierarquia/superiores/1")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getSuperioresByUsuario_deveRetornarNada_quandoUsuarioNaoExistir() throws Exception {
        doReturn(Collections.emptyList())
            .when(usuarioService).getSuperioresDoUsuario(anyInt());

        mvc.perform(get("/api/usuarios/hierarquia/superiores/999")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getSuperioresByUsuario_deveRetornarUnauthorized_quandoNaoInformarToken() throws Exception {
        mvc.perform(get("/api/usuarios/hierarquia/superiores/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getSuperioresByUsuarioPorCargo_deveRetornar_quandoForValido() throws Exception {
        doReturn(Collections.singletonList(umUsuarioHierarquia()))
            .when(usuarioService).getSuperioresDoUsuarioPorCargo(anyInt(), any());

        mvc.perform(get("/api/usuarios/hierarquia/superiores/1/COORDENADOR_OPERACAO")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(100)))
            .andExpect(jsonPath("$[0].nome", is("XBRAIN")));
    }

    @Test
    public void getSuperioresByUsuarioPorCargo_deveRetornarNada_quandoNaoPossuirSuperior() throws Exception {
        doReturn(Collections.emptyList())
            .when(usuarioService).getSuperioresDoUsuarioPorCargo(anyInt(), any());

        mvc.perform(get("/api/usuarios/hierarquia/superiores/1/COORDENADOR_OPERACAO")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getSuperioresByUsuarioPorCargo_deveRetornarNada_quandoUsuarioNaoExistir() throws Exception {
        doReturn(Collections.emptyList())
            .when(usuarioService).getSuperioresDoUsuarioPorCargo(anyInt(), any());

        mvc.perform(get("/api/usuarios/hierarquia/superiores/999")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getSuperioresByUsuarioPorCargo_deveRetornarUnauthorized_quandoNaoInformarToken() throws Exception {
        mvc.perform(get("/api/usuarios/hierarquia/superiores/1/COORDENADOR_OPERACAO"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getSuperioresByUsuarioPorCargo_deveRetornarBadRequest_quandoCargoNaoExistir() throws Exception {
        mvc.perform(get("/api/usuarios/hierarquia/superiores/1/a")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void getUsuariosParaDistribuicaoDeAgendamentos_deveRetornarForbidden_quandoUsuarioNaoPossuirPermissao()
            throws Exception {
        mvc.perform(get(URL_USUARIOS_AGENDAMENTOS + "131/agenteautorizado/1300")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, HELP_DESK)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getUsuariosInativosByIds_deveRetornarUsuariosInativos_quandoForPassadoIds() throws Exception {

        when(usuarioService.getUsuariosInativosByIds(List.of(101, 102, 103)))
                .thenReturn(List.of(umUsuarioResponseInativo(101),
                        umUsuarioResponseInativo(102),
                        umUsuarioResponseInativo(103)));

        mvc.perform(get("/api/usuarios/inativos?usuariosInativosIds=101,102,103")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(101)))
                .andExpect(jsonPath("$[0].situacao", is(ESituacao.I.name())))
                .andExpect(jsonPath("$[1].id", is(102)))
                .andExpect(jsonPath("$[1].situacao", is(ESituacao.I.name())))
                .andExpect(jsonPath("$[2].id", is(103)))
                .andExpect(jsonPath("$[2].situacao", is(ESituacao.I.name())));
    }

    @Test
    public void getUsuariosParaDistribuicaoDeAgendamentos_deveRetornar200_seUsuarioPossuirPermissao() throws Exception {
        mvc.perform(get(URL_USUARIOS_AGENDAMENTOS + "131/agenteautorizado/1300")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(4)))
            .andExpect(jsonPath("$[0].id", is(130)))
            .andExpect(jsonPath("$[0].nome", is("JOÃO MARINHO DA SILVA DOS SANTOS")))
            .andExpect(jsonPath("$[1].id", is(133)))
            .andExpect(jsonPath("$[1].nome", is("JOSÉ MARINHO DA SILVA DOS SANTOS JÚNIOR")))
            .andExpect(jsonPath("$[2].id", is(134)))
            .andExpect(jsonPath("$[2].nome", is("MARIA DA SILVA SAURO SANTOS")))
            .andExpect(jsonPath("$[3].id", is(135)))
            .andExpect(jsonPath("$[3].nome", is("MARCOS AUGUSTO DA SILVA SANTOS")));
    }

    @Test
    public void getUsuariosExecutivos_deveRetornarStatusCode200() throws Exception {
        when(usuarioService.buscarExecutivosPorSituacao(ESituacao.A))
            .thenReturn(List.of(umUsuarioExecutivo(1, "seiya@cdz.com", "SEIYA"),
                umUsuarioExecutivo(2, "ikki@cdz.com", "IKKI")));

        mvc.perform(get(USUARIOS_ENDPOINT + "/executivos")
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].nome", is("SEIYA")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].nome", is("IKKI")));
    }

    @Test
    public void findUsuariosByIds_deveRetornarUsuarios_quandoForPassadoIdsDosUsuarios() throws Exception {
        when(usuarioService.findUsuariosByIds(List.of(100, 101)))
            .thenReturn(List.of(
                    umUsuarioSituacaoResponse(100, "ADMIN", ESituacao.A ),
                    umUsuarioSituacaoResponse(101, "HELPDESK", ESituacao.A)));

        mvc.perform(get(USUARIOS_ENDPOINT + "/usuario-situacao")
            .param("usuariosIds", "100,101")
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(100)))
            .andExpect(jsonPath("$[0].nome", is("ADMIN")))
            .andExpect(jsonPath("$[0].situacao", is(ESituacao.A.name())))
            .andExpect(jsonPath("$[1].id", is(101)))
            .andExpect(jsonPath("$[1].nome", is("HELPDESK")))
            .andExpect(jsonPath("$[1].situacao", is(ESituacao.A.name())));
    }

    @Test
    public void findById_deveRetornarUsuarioResponseSemPermissoes_quandoSolicitado() throws Exception {
        doReturn(UsuarioResponse.builder()
            .id(1)
            .nome("RENATO")
            .situacao(ESituacao.A)
            .email("RENATO@GMAIL.COM")
            .build())
            .when(usuarioService).findById(1);

        mvc.perform(get("/api/usuarios/1/sem-permissoes")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.nome", is("RENATO")))
            .andExpect(jsonPath("$.situacao", is("A")))
            .andExpect(jsonPath("$.email", is("RENATO@GMAIL.COM")))
            .andExpect(jsonPath("$.permissoes", nullValue()));
    }

    @Test
    public void findUsuariosByCodigoCargo_deveRetornar400_quandoInformarUmCodigoCargoNaoExistente()
        throws Exception {
        mvc.perform(get("/api/usuarios/cargo/UM_CODIGO_CARGO_NAO_EXISTENTE")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isBadRequest());

        verify(usuarioService, times(0)).findUsuariosByCodigoCargo(any());
    }

    @Test
    public void findUsuariosByCodigoCargo_deveRetornar200_quandoInformarUmCargoCodigoExistente()
        throws Exception {
        doReturn(umaListaUsuariosExecutivosAtivo())
            .when(usuarioService).findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO);

        mvc.perform(get("/api/usuarios/cargo/EXECUTIVO")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].nome", is("RENATO")))
            .andExpect(jsonPath("$[0].situacao", is("A")))
            .andExpect(jsonPath("$[0].email", is("RENATO@GMAIL.COM")));

        verify(usuarioService, times(1)).findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO);
    }

    private List<UsuarioResponse> umaListaUsuariosExecutivosAtivo() {
        return List.of(
            UsuarioResponse.builder()
                .id(1)
                .nome("RENATO")
                .situacao(ESituacao.A)
                .email("RENATO@GMAIL.COM")
                .build(),
            UsuarioResponse.builder()
                .id(2)
                .nome("VALDECIR")
                .situacao(ESituacao.A)
                .email("VALDECIR@GMAIL.COM")
                .build()
        );
    }

    private UsuarioResponse umUsuarioResponseInativo(Integer id) {
        return UsuarioResponse.builder()
            .id(id)
            .situacao(ESituacao.I)
            .build();
    }

    private static UsuarioExecutivoResponse umUsuarioExecutivo(Integer id, String email, String nome) {
        return new UsuarioExecutivoResponse(id, email, nome);
    }

    private static UsuarioSituacaoResponse umUsuarioSituacaoResponse(Integer id, String nome, ESituacao situacao) {
        return UsuarioSituacaoResponse
            .builder()
            .id(id)
            .nome(nome)
            .situacao(situacao)
            .build();
    }
}
