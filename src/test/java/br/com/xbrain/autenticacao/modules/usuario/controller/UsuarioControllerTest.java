package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.service.DeslogarUsuarioPorExcessoDeUsoService;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.permissao.service.JsonWebTokenService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.repository.ConfiguracaoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.SubCanalService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioAgendamentoService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import helpers.Usuarios;
import lombok.SneakyThrows;
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

import static br.com.xbrain.autenticacao.modules.feeder.helper.VendedoresFeederFiltrosHelper.umVendedoresFeederFiltros;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.usuariosMesmoSegmentoAgenteAutorizado1300;
import static helpers.TestBuilders.*;
import static helpers.TestsHelper.*;
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
    @MockBean
    private AgenteAutorizadoNovoService agenteAutorizadoNovoService;
    @MockBean
    private DeslogarUsuarioPorExcessoDeUsoService deslogarUsuarioPorExcessoDeUsoService;
    @MockBean
    private FeederService feederService;
    @MockBean
    private SubCanalService subCanalService;

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

    private static UsuarioResponse umUsuarioResponse(Integer id, String nome, String email, ESituacao situacao) {
        return UsuarioResponse
            .builder()
            .id(id)
            .nome(nome)
            .email(email)
            .situacao(situacao)
            .build();
    }

    private static UsuarioResponse umUsuarioResponse(Integer id, String nome, String cpf, String email, ESituacao situacao) {
        return UsuarioResponse
            .builder()
            .id(id)
            .nome(nome)
            .cpf(cpf)
            .email(email)
            .situacao(situacao)
            .build();
    }

    private static UsuarioAgenteAutorizadoResponse umUsuarioAgenteAutorizadoResponse(Integer id, Integer aaId) {
        return UsuarioAgenteAutorizadoResponse.builder()
            .id(id)
            .nome("FULANO DE TESTE")
            .email("TESTE@TESTE.COM")
            .agenteAutorizadoId(aaId)
            .build();
    }

    @Before
    public void setup() {
        when(autenticacaoService.getUsuarioId())
            .thenReturn(100);
        when(usuarioAgendamentoService.recuperarUsuariosParaDistribuicao(eq(131), eq(1300)))
            .thenReturn(usuariosMesmoSegmentoAgenteAutorizado1300());
    }

    @Test
    @SneakyThrows
    public void buscarNaoRealocadosPorCpf_deveSolicitarUsuarioNaoRealocado_quandoTiverAutorizacao() {
        mvc.perform(get("/api/usuarios/nao-realocado")
                .param("cpf", "65710871036")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void buscarNaoRealocadosPorCpf_deveRetornar401_quandoNaoTiverAutorizacao() {
        mvc.perform(get("/api/usuarios/nao-realocado")
                .param("cpf", "65710871036")
                .header("Authorization", getAccessToken(mvc, INATIVO))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void deveSolicitarAtivacaoUsuario() {
        mvc.perform(put("/api/usuarios/ativar/999")
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void deveSolicitarInativacaoUsuario() {
        mvc.perform(put("/api/usuarios/inativar/999")
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());
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
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void deveNaoRetornarUsuariosPorIdsInativos() throws Exception {
        mvc.perform(get("/api/usuarios?ids=105")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getUsuarioVendedorById_deveRetornarUsuariosPorIds() throws Exception {
        mvc.perform(get("/api/usuarios/vendedores")
                .param("ids", "100,101")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(100)))
            .andExpect(jsonPath("$[0].email", is("ADMIN@XBRAIN.COM.BR")))
            .andExpect(jsonPath("$[1].id", is(101)))
            .andExpect(jsonPath("$[1].email", is("HELPDESK@XBRAIN.COM.BR")));
    }

    @Test
    public void deveRetornarUsuarioPorIdEVerificarPermissoes() throws Exception {
        mvc.perform(get("/api/usuarios/100")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(100)))
            .andExpect(jsonPath("$.nome", is("ADMIN")))
            .andExpect(jsonPath("$.email", is("ADMIN@XBRAIN.COM.BR")))
            .andExpect(jsonPath("$.permissoes[1]", is("ROLE_AUT_ATUALIZAR_SENHA_REENVIAR_EMAIL")));
    }

    @Test
    public void deveRetornarUsuarioAutenticadoPorId() throws Exception {
        mvc.perform(get("/api/usuarios/autenticado/101")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(101)))
            .andExpect(jsonPath("$.nome", is("HELPDESK")))
            .andExpect(jsonPath("$.email", is("HELPDESK@XBRAIN.COM.BR")));
    }

    @Test
    @SneakyThrows
    public void getUsuarioAutenticadoComLoginNetSalesById_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get("/api/usuarios/autenticado-com-login-netsales/101")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void getUsuarioAutenticadoComLoginNetSalesById_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get("/api/usuarios/autenticado-com-login-netsales/227")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void getUsuarioByCpf_deveRetornarUsuarioPorCpf_naoInformandoFiltro() throws Exception {
        mvc.perform(get("/api/usuarios")
                .param("cpf", "65710871036")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(101)))
            .andExpect(jsonPath("$.nome", is("HELPDESK")))
            .andExpect(jsonPath("$.email", is(Usuarios.HELP_DESK)))
            .andExpect(jsonPath("$.situacao", is("A")));
    }

    @Test
    public void getUsuarioByCpf_deveRetornarUsuarioPorCpf_ignorandoBuscaPorSomenteSituacaoAtivo() throws Exception {
        mvc.perform(get("/api/usuarios")
                .param("cpf", "41842888803")
                .param("buscarAtivo", "false")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(105)))
            .andExpect(jsonPath("$.nome", is("INATIVO")))
            .andExpect(jsonPath("$.email", is(INATIVO)))
            .andExpect(jsonPath("$.situacao", is("I")));
    }

    @Test
    public void getUsuarioByCpf_deveRetornarUsuarioPorCpf_comSituacaoAtivo() throws Exception {
        mvc.perform(get("/api/usuarios")
                .param("cpf", "28667582506")
                .param("buscarAtivo", "true")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(366)))
            .andExpect(jsonPath("$.nome", is("Mso Analista Adm Claro Pessoal")))
            .andExpect(jsonPath("$.email", is(MSO_ANALISTAADM_CLAROMOVEL_PESSOAL)))
            .andExpect(jsonPath("$.situacao", is("A")));
    }

    @Test
    public void getUsuarioByCpf_deveRetornar200ComResponseBodyVazio_quandoCpfNaoEncontrado() throws Exception {
        mvc.perform(get("/api/usuarios")
                .param("cpf", "12345678901")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void getUsuarioByCpf_deveRetornar200ComResponseBodyVazio_quandoUsuarioForInativoOuRealocadoBuscandoApenasUsuarioAtivo()
        throws Exception {

        mvc.perform(get("/api/usuarios")
                .param("cpf", "41842888803")
                .param("buscarAtivo", "true")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void getUsuariosByCpfs_deveRetornarUsuarioPorCpf_ignorandoBuscaPorSomenteSituacaoAtivo() throws Exception {
        doReturn(List.of(
                umUsuarioResponse(
                    106,
                    "Usuario Inativo",
                    "68731547257",
                    INATIVO,
                    ESituacao.I
                ), umUsuarioResponse(
                    107,
                    "Usuario Ativo",
                    "44215764173",
                    ATIVO,
                    ESituacao.A
                )
            )
        )
            .when(usuarioService)
            .findByCpfs(List.of("68731547257", "44215764173"), false);

        mvc.perform(post("/api/usuarios/cpfs")
                .param("buscarAtivo", "false")
                .content(convertObjectToJsonBytes(List.of("68731547257", "44215764173")))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(106)))
            .andExpect(jsonPath("$[0].nome", is("Usuario Inativo")))
            .andExpect(jsonPath("$[0].cpf", is("68731547257")))
            .andExpect(jsonPath("$[0].email", is(INATIVO)))
            .andExpect(jsonPath("$[0].situacao", is("I")))
            .andExpect(jsonPath("$[1].id", is(107)))
            .andExpect(jsonPath("$[1].nome", is("Usuario Ativo")))
            .andExpect(jsonPath("$[1].cpf", is("44215764173")))
            .andExpect(jsonPath("$[1].email", is(ATIVO)))
            .andExpect(jsonPath("$[1].situacao", is("A")));
    }

    @Test
    public void getUsuariosByCpfs_deveRetornarUsuarioPorCpf_comSituacaoAtivo() throws Exception {
        doReturn(List.of(
                umUsuarioResponse(
                    107,
                    "Usuario Ativo 1",
                    "44215764173",
                    ATIVO,
                    ESituacao.A
                ), umUsuarioResponse(
                    108,
                    "Usuario Ativo 2",
                    "09667546977",
                    "ATIVO2@XBRAIN.COM.BR",
                    ESituacao.A
                ), umUsuarioResponse(
                    109,
                    "Usuario Ativo 3",
                    "72489645498",
                    "ATIVO3@XBRAIN.COM.BR",
                    ESituacao.A
                )
            )
        )
            .when(usuarioService)
            .findByCpfs(List.of("68731547257", "44215764173", "09667546977", "72489645498"), true);

        mvc.perform(post("/api/usuarios/cpfs")
                .param("buscarAtivo", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(List.of("68731547257", "44215764173", "09667546977", "72489645498")))
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(107)))
            .andExpect(jsonPath("$[0].nome", is("Usuario Ativo 1")))
            .andExpect(jsonPath("$[0].cpf", is("44215764173")))
            .andExpect(jsonPath("$[0].email", is(ATIVO)))
            .andExpect(jsonPath("$[0].situacao", is("A")))
            .andExpect(jsonPath("$[1].id", is(108)))
            .andExpect(jsonPath("$[1].nome", is("Usuario Ativo 2")))
            .andExpect(jsonPath("$[1].cpf", is("09667546977")))
            .andExpect(jsonPath("$[1].email", is("ATIVO2@XBRAIN.COM.BR")))
            .andExpect(jsonPath("$[1].situacao", is("A")))
            .andExpect(jsonPath("$[2].id", is(109)))
            .andExpect(jsonPath("$[2].nome", is("Usuario Ativo 3")))
            .andExpect(jsonPath("$[2].cpf", is("72489645498")))
            .andExpect(jsonPath("$[2].email", is("ATIVO3@XBRAIN.COM.BR")))
            .andExpect(jsonPath("$[2].situacao", is("A")));
    }

    @Test
    public void getUsuarioByEmail_deveRetornarOUsuarioPorEmail_naoInformandoFiltro() throws Exception {
        mvc.perform(get("/api/usuarios")
                .param("email", "ADMIN@XBRAIN.COM.BR")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(100)))
            .andExpect(jsonPath("$.nome", is("ADMIN")))
            .andExpect(jsonPath("$.email", is(ADMIN)))
            .andExpect(jsonPath("$.situacao", is("A")));
    }

    @Test
    public void getUsuarioByEmail_deveRetornarOUsuarioPorEmail_ignorandoBuscaPorSomenteSituacaoAtivo() throws Exception {
        mvc.perform(get("/api/usuarios")
                .param("email", "INATIVO@XBRAIN.COM.BR")
                .param("buscarAtivo", "false")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(105)))
            .andExpect(jsonPath("$.nome", is("INATIVO")))
            .andExpect(jsonPath("$.email", is(INATIVO)))
            .andExpect(jsonPath("$.situacao", is("I")));
    }

    @Test
    public void getUsuarioByEmail_deveRetornarUsuarioPorEmail_comSituacaoAtivo() throws Exception {
        mvc.perform(get("/api/usuarios")
                .param("email", "MSO_ANALISTAADM_CLAROMOVEL_PESSOAL@NET.COM.BR")
                .param("buscarAtivo", "true")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(366)))
            .andExpect(jsonPath("$.nome", is("Mso Analista Adm Claro Pessoal")))
            .andExpect(jsonPath("$.email", is(MSO_ANALISTAADM_CLAROMOVEL_PESSOAL)))
            .andExpect(jsonPath("$.situacao", is("A")));
    }

    @Test
    public void getUsuarioByEmail_deveRetornar200ComResponseBodyVazio_quandoEmailNaoEncontrado() throws Exception {
        mvc.perform(get("/api/usuarios")
                .param("email", "TESTE@TESTE.COM")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void getUsuarioByEmail_deveRetornar200ComResponseBodyVazio_quandoUsuarioForInativoOuRealocadoBuscandoPorUsuarioAtivo()
        throws Exception {

        mvc.perform(get("/api/usuarios")
                .param("email", "INATIVO@XBRAIN.COM.BR")
                .param("buscarAtivo", "true")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void getUsuariosByEmails_deveRetornarUsuariosPorEmails_ignorandoBuscaPorSomenteSituacaoAtivo() throws Exception {
        doReturn(List.of(
                umUsuarioResponse(
                    106,
                    "Usuario Inativo",
                    INATIVO,
                    ESituacao.I
                ), umUsuarioResponse(
                    107,
                    "Usuario Ativo",
                    ATIVO,
                    ESituacao.A
                )
            )
        )
            .when(usuarioService)
            .findByEmails(List.of(INATIVO, ATIVO), false);

        mvc.perform(post("/api/usuarios/emails")
                .param("buscarAtivo", "false")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(List.of("INATIVO@XBRAIN.COM.BR", "ATIVO@XBRAIN.COM.BR")))
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(106)))
            .andExpect(jsonPath("$[0].nome", is("Usuario Inativo")))
            .andExpect(jsonPath("$[0].email", is(INATIVO)))
            .andExpect(jsonPath("$[0].situacao", is("I")))
            .andExpect(jsonPath("$[1].id", is(107)))
            .andExpect(jsonPath("$[1].nome", is("Usuario Ativo")))
            .andExpect(jsonPath("$[1].email", is(ATIVO)))
            .andExpect(jsonPath("$[1].situacao", is("A")));
    }

    @Test
    public void getUsuariosByEmails_deveRetornarUsuariosPorEmails_comSituacaoAtivo() throws Exception {
        doReturn(List.of(
                umUsuarioResponse(
                    107,
                    "Usuario Ativo 1",
                    ATIVO,
                    ESituacao.A
                ),
                umUsuarioResponse(
                    108,
                    "Usuario Ativo 2",
                    "ATIVO2@XBRAIN.COM.BR",
                    ESituacao.A
                ),
                umUsuarioResponse(
                    109,
                    "Usuario Ativo 3",
                    "ATIVO3@XBRAIN.COM.BR",
                    ESituacao.A
                )
            )
        )
            .when(usuarioService)
            .findByEmails(List.of(
                INATIVO,
                ATIVO,
                "ATIVO2@XBRAIN.COM.BR",
                "ATIVO3@XBRAIN.COM.BR"), true);

        mvc.perform(post("/api/usuarios/emails")
                .content(convertObjectToJsonBytes(
                    List.of("INATIVO@XBRAIN.COM.BR", "ATIVO@XBRAIN.COM.BR", "ATIVO2@XBRAIN.COM.BR", "ATIVO3@XBRAIN.COM.BR")
                ))
                .param("buscarAtivo", "true")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(107)))
            .andExpect(jsonPath("$[0].nome", is("Usuario Ativo 1")))
            .andExpect(jsonPath("$[0].email", is(ATIVO)))
            .andExpect(jsonPath("$[0].situacao", is("A")))
            .andExpect(jsonPath("$[1].id", is(108)))
            .andExpect(jsonPath("$[1].nome", is("Usuario Ativo 2")))
            .andExpect(jsonPath("$[1].email", is("ATIVO2@XBRAIN.COM.BR")))
            .andExpect(jsonPath("$[1].situacao", is("A")))
            .andExpect(jsonPath("$[2].id", is(109)))
            .andExpect(jsonPath("$[2].nome", is("Usuario Ativo 3")))
            .andExpect(jsonPath("$[2].email", is("ATIVO3@XBRAIN.COM.BR")))
            .andExpect(jsonPath("$[2].situacao", is("A")));
    }

    @Test
    public void deveRetornarAsEmpresasDoUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/100/empresas")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void deveRetornarUsuariosPorNivelDoCargo() throws Exception {
        mvc.perform(get("/api/usuarios?nivel=XBRAIN")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    public void deveRetornarUsuariosPorPermissaoEspecial() throws Exception {
        mvc.perform(get("/api/usuarios?funcionalidade=POL_AGENTE_AUTORIZADO_APROVACAO_MSO_NOVO_CADASTRO")
                .header("Authorization", getAccessToken(mvc, ADMIN))
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
        verify(emailService, times(1)).enviarEmailTemplate(any(), any(), any(), any(), any());
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
        verify(emailService, times(1)).enviarEmailTemplate(any(), any(), any(), any(), any());
    }

    @Test
    public void getPermissoesPorCanal_permissoesComCanal_somentePermitidasAoUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/permissoes-por-canal")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(15)))
            .andExpect(jsonPath("$[0].permissao", is("ROLE_AUT_2031")))
            .andExpect(jsonPath("$[0].canais", hasSize(2)))
            .andExpect(jsonPath("$[0].canais[0]", is("AGENTE_AUTORIZADO")))
            .andExpect(jsonPath("$[0].canais[1]", is("ATIVO_PROPRIO")));
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
    public void getUfsUsuario_deveRetornarOsEstados_conformeUsuarioIdInformado() throws Exception {
        mvc.perform(get("/api/usuarios/100/ufs")
            .header("Authorization", getAccessToken(mvc, SOCIO_AA))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].value", is(1)))
            .andExpect(jsonPath("$[0].label", is("PARANA")));
    }

    @Test
    public void getPermissoesPorUsuarios_throwException_QuandoParametrosVazios() throws Exception {
        mvc.perform(post("/api/usuarios/permissoes-por-usuario")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new UsuarioPermissoesRequest())))
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
        var request = new UsuarioPermissoesRequest(List.of(2844), List.of("ROLE_VDS_TABULACAO_CLICKTOCALL"));
        mvc.perform(post("/api/usuarios/permissoes-por-usuario")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(request)))
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
                umUsuarioSituacaoResponse(100, "ADMIN", ESituacao.A),
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

    @Test
    @SneakyThrows
    public void findIdUsuariosAtivosByCodigoCargos_deveRetornarListaIdUsuariosAtivos_pelosCodigosDosCargos() {
        var codigoCargos = List.of(ADMINISTRADOR, GERENTE_OPERACAO);
        mvc.perform(get("/api/usuarios/cargos")
                .param("codigoCargos", "ADMINISTRADOR, GERENTE_OPERACAO")
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());

        verify(usuarioService, times(1)).findIdUsuariosAtivosByCodigoCargos(eq(codigoCargos));
    }

    @Test
    @SneakyThrows
    public void findIdUsuariosAtivosByCodigoCargos_deveRetornarUnauthorized_quandoNaoInformarToken() {
        mvc.perform(get("/api/usuarios/cargos")
                .param("codigoCargos", "ADMINISTRADOR, GERENTE_OPERACAO"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void buscarUsuariosDaHierarquiaDoUsuarioLogadoPorCargp_deveRetornarOsUsuariosDaHierarquia() {
        doReturn(List.of(
            SelectResponse.of(1, "Teste"),
            SelectResponse.of(2, "Brandon")))
            .when(usuarioService).buscarUsuariosDaHierarquiaDoUsuarioLogado(null);

        mvc.perform(get("/api/usuarios/permitidos/select")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].value", is(1)))
            .andExpect(jsonPath("$[0].label", is("Teste")))
            .andExpect(jsonPath("$[1].value", is(2)))
            .andExpect(jsonPath("$[1].label", is("Brandon")));

        verify(usuarioService, times(1)).buscarUsuariosDaHierarquiaDoUsuarioLogado(isNull());
    }

    @Test
    @SneakyThrows
    public void findUsuarioAlvoDosComunicados_deveRetornarUsuarios_quandoFornecerRegionalId() {
        doReturn(List.of(
            UsuarioNomeResponse.of(1, "Teste", ESituacao.A),
            UsuarioNomeResponse.of(2, "Brandon", ESituacao.A)))
            .when(usuarioService).getUsuariosAlvoDoComunicado(any(PublicoAlvoComunicadoFiltros.class));

        mvc.perform(get("/api/usuarios/alvo/comunicado")
            .param("regionalId", "1027")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].nome", is("Teste")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].nome", is("Brandon")));

        verify(usuarioService, times(1)).getUsuariosAlvoDoComunicado(
            eq(PublicoAlvoComunicadoFiltros.builder()
                .regionalId(1027)
                .build()));
    }

    @Test
    @SneakyThrows
    public void findUsuarioAlvoDosComunicados_deveRetornarUsuarios_quandoFornecerUfId() {
        doReturn(List.of(
            UsuarioNomeResponse.of(1, "Teste", ESituacao.A),
            UsuarioNomeResponse.of(2, "Brandon", ESituacao.A)))
            .when(usuarioService).getUsuariosAlvoDoComunicado(any(PublicoAlvoComunicadoFiltros.class));

        mvc.perform(get("/api/usuarios/alvo/comunicado")
            .param("regionalId", "1027")
            .param("ufId", "1")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].nome", is("Teste")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].nome", is("Brandon")));

        verify(usuarioService, times(1)).getUsuariosAlvoDoComunicado(
            eq(PublicoAlvoComunicadoFiltros.builder()
                .ufId(1)
                .regionalId(1027)
                .build()));
    }

    @Test
    @SneakyThrows
    public void findUsuarioAlvoDosComunicados_deveRetornarUsuarios_quandoFornecerCidadesIds() {
        doReturn(List.of(
            UsuarioNomeResponse.of(1, "Teste", ESituacao.A),
            UsuarioNomeResponse.of(2, "Brandon", ESituacao.A)))
            .when(usuarioService).getUsuariosAlvoDoComunicado(any(PublicoAlvoComunicadoFiltros.class));

        mvc.perform(get("/api/usuarios/alvo/comunicado")
            .param("regionalId", "1027")
            .param("ufId", "1")
            .param("cidadesIds", "5578")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].nome", is("Teste")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].nome", is("Brandon")));

        verify(usuarioService, times(1)).getUsuariosAlvoDoComunicado(
            eq(PublicoAlvoComunicadoFiltros.builder()
                .ufId(1)
                .regionalId(1027)
                .cidadesIds(List.of(5578))
                .build()));
    }

    @Test
    @SneakyThrows
    public void getUsuarioByIdComLoginNetSales_deveRetornarOk_seUsuarioPossuirLoginNetSales() {
        final var umUsuarioId = 227;

        mvc.perform(get("/api/usuarios/{id}/com-login-netsales", umUsuarioId)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(227)))
            .andExpect(jsonPath("$.nome", is("VENDEDOR AA")))
            .andExpect(jsonPath("$.loginNetSales", is("um login netsales")))
            .andExpect(jsonPath("$.nivelCodigo", is("AGENTE_AUTORIZADO")));
    }

    @Test
    @SneakyThrows
    public void getUsuarioByIdComLoginNetSales_deveRetornarBadRequest_seUsuarioNaoPossuirLoginNetSales() {
        final var umUsuarioId = 226;

        mvc.perform(get("/api/usuarios/{id}/com-login-netsales", umUsuarioId)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "Usuário não possui login NetSales válido.")));
    }

    @Test
    @SneakyThrows
    public void getUsuarioByIdComLoginNetSales_deveRetornarBadRequest_seUsuarioNaoEncontrado() {
        final var umUsuarioId = 999;

        mvc.perform(get("/api/usuarios/{id}/com-login-netsales", umUsuarioId)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "Usuário não encontrado.")));
    }

    @Test
    @SneakyThrows
    public void getUsuarioByIdComLoginNetSales_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        final var umUsuarioId = 1000;

        mvc.perform(get("/api/usuarios/{id}/com-login-netsales", umUsuarioId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void buscarUsuariosAtivosNivelOperacao_deveRetornarAtivosOperacao_quandoCanalAgenteAutorizado() throws Exception {
        mvc.perform(get("/api/usuarios/ativos/nivel/operacao/canal-aa")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].value").value(102))
            .andExpect(jsonPath("$[0].label").value("Supervisor Operação"))
            .andExpect(jsonPath("$[1].value").value(300))
            .andExpect(jsonPath("$[1].label").value("Operacao Supervisor NET"));
    }

    @Test
    public void buscarUrlLojaOnline_deveRetornarUrls_quandoSolicitado() throws Exception {
        mvc.perform(get("/api/usuarios/100/url-loja-online")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.urlLojaBase", is("http://loja.com.br/1")))
            .andExpect(jsonPath("$.urlLojaProspect", is("http://loja.com.br/2")))
            .andExpect(jsonPath("$.urlLojaProspectNextel", is("http://loja.com.br/3")));
    }

    @Test
    public void buscarUrlLojaOnline_deveRetornarBadRequest_quandoNaoEncontrado() throws Exception {
        mvc.perform(get("/api/usuarios/99999/url-loja-online")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void buscarBackOfficesAndSociosAaPorAaIds_deveRetornarListaVazia_quandoNaoEncontrado() throws Exception {
        when(usuarioService.buscarBackOfficesAndSociosAaPorAaIds(anyList())).thenReturn(Collections.emptyList());
        mvc.perform(get(USUARIOS_ENDPOINT + "/backoffices-socios-por-agentes-autorizado-id")
                .param("agentesAutorizadoId", "100,101")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(Collections.emptyList())));
    }

    @Test
    public void buscarBackOfficesAndSociosAaPorAaIds_deveRetornarUsuarios_quandoEncontrado() throws Exception {
        when(usuarioService.buscarBackOfficesAndSociosAaPorAaIds(anyList())).thenReturn(List.of(
            umUsuarioAgenteAutorizadoResponse(100, 100),
            umUsuarioAgenteAutorizadoResponse(101, 101)));
        mvc.perform(get(USUARIOS_ENDPOINT + "/backoffices-socios-por-agentes-autorizado-id")
                .param("agentesAutorizadoId", "100,101")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(100)))
            .andExpect(jsonPath("$[0].nome", is("FULANO DE TESTE")))
            .andExpect(jsonPath("$[0].email", is("TESTE@TESTE.COM")))
            .andExpect(jsonPath("$[0].agenteAutorizadoId", is(100)))
            .andExpect(jsonPath("$[1].id", is(101)))
            .andExpect(jsonPath("$[1].nome", is("FULANO DE TESTE")))
            .andExpect(jsonPath("$[1].email", is("TESTE@TESTE.COM")))
            .andExpect(jsonPath("$[1].agenteAutorizadoId", is(101)));
    }

    @Test
    @SneakyThrows
    public void buscarVendedoresFeeder_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get("/api/usuarios/vendedores-feeder")
                .param("aasIds", "1")
                .param("comSocioPrincipal", "true")
                .header("Authorization", "")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(usuarioService, never()).buscarVendedoresFeeder(any());
    }

    @Test
    @SneakyThrows
    public void buscarVendedoresFeeder_deveRetornarForbidden_quandoUsuarioAutenticadoESemPermissao() {
        mvc.perform(get("/api/usuarios/vendedores-feeder")
                .param("aasIds", "1")
                .param("comSocioPrincipal", "true")
                .header("Authorization", getAccessToken(mvc, HELP_DESK))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(usuarioService, never()).buscarVendedoresFeeder(any());
    }

    @Test
    @SneakyThrows
    public void buscarVendedoresFeeder_deveRetornarBadRequest_quandoFiltrosObrigatoriosNaoInformados() {
        mvc.perform(get("/api/usuarios/vendedores-feeder")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo aasIds é obrigatório.",
                "O campo comSocioPrincipal é obrigatório.")));

        verify(usuarioService, never()).buscarVendedoresFeeder(any());
    }

    @Test
    @SneakyThrows
    public void buscarVendedoresFeeder_deveRetornarOk_quandoFiltrosObrigatoriosInformados() {
        mvc.perform(get("/api/usuarios/vendedores-feeder")
                .param("aasIds", "1")
                .param("comSocioPrincipal", "true")
                .param("buscarInativos", "true")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService, times(1)).buscarVendedoresFeeder(eq(umVendedoresFeederFiltros(List.of(1), true, true)));
    }

    @Test
    @SneakyThrows
    public void obterNomeUsuarioPorId_deveRetornarOk_quandoUsuarioEncontrado() {
        mvc.perform(get("/api/usuarios/100/nome")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void buscarUsuarioSituacaoPorIds_unauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(post("/api/usuarios/usuario-situacao/por-ids")
                .content(convertObjectToJsonBytes(new UsuarioSituacaoFiltro(List.of(1, 2, 3))))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(usuarioService, never()).buscarUsuarioSituacaoPorIds(any());
    }

    @Test
    @SneakyThrows
    public void buscarUsuarioSituacaoPorIds_badRequest_seListaVazia() {
        mvc.perform(post("/api/usuarios/usuario-situacao/por-ids")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .content(convertObjectToJsonBytes(new UsuarioSituacaoFiltro(List.of())))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder("O campo usuariosIds é obrigatório.")));

        verify(usuarioService, never()).buscarUsuarioSituacaoPorIds(any());
    }

    @Test
    @SneakyThrows
    public void buscarUsuarioSituacaoPorIds_ok_seListaNaoVazia() {
        mvc.perform(post("/api/usuarios/usuario-situacao/por-ids")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .content(convertObjectToJsonBytes(new UsuarioSituacaoFiltro(List.of(1, 2, 3))))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService, times(1)).buscarUsuarioSituacaoPorIds(eq(new UsuarioSituacaoFiltro(List.of(1, 2, 3))));
    }

    @Test
    @SneakyThrows
    public void findUsuariosOperadoresBackofficeByOrganizacao_deveBuscarComFlagTrue_seFlagBuscarInativosNaoEnviada() {
        mvc.perform(get("/api/usuarios")
                .param("organizacaoId", "5")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService, times(1))
            .findUsuariosOperadoresBackofficeByOrganizacao(eq(5), eq(true));
    }

    @Test
    @SneakyThrows
    public void findUsuariosOperadoresBackofficeByOrganizacao_deveBuscarComFlagTrue_seFlagBuscarInativosForTrue() {
        mvc.perform(get("/api/usuarios")
                .param("organizacaoId", "5")
                .param("buscarInativos", "true")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService, times(1))
            .findUsuariosOperadoresBackofficeByOrganizacao(eq(5), eq(true));
    }

    @Test
    @SneakyThrows
    public void findUsuariosOperadoresBackofficeByOrganizacao_deveBuscarComFlagFalse_seFlagBuscarInativosForFalse() {
        mvc.perform(get("/api/usuarios")
                .param("organizacaoId", "5")
                .param("buscarInativos", "false")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService, times(1))
            .findUsuariosOperadoresBackofficeByOrganizacao(eq(5), eq(false));
    }

    @Test
    @SneakyThrows
    public void getAllVendedoresReceptivos_deveRetornarStatusOk_quandoValido() {
        mvc.perform(get(USUARIOS_ENDPOINT + "/vendedores-receptivos")
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].label", is("VR 1")))
            .andExpect(jsonPath("$[0].value", is(420)))
            .andExpect(jsonPath("$[1].label", is("VR 2 (REALOCADO)")))
            .andExpect(jsonPath("$[1].value", is(421)))
            .andExpect(jsonPath("$[2].label", is("VR 3")))
            .andExpect(jsonPath("$[2].value", is(422)));
    }

    @Test
    @SneakyThrows
    public void getAllVendedoresReceptivosById_deveRetornarStatusOk_quandoValido() {
        mvc.perform(get(USUARIOS_ENDPOINT + "/vendedores-receptivos/por-ids")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .param("ids", "421,422"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(421)))
            .andExpect(jsonPath("$[0].nome", is("VR 2 (REALOCADO)")))
            .andExpect(jsonPath("$[1].id", is(422)))
            .andExpect(jsonPath("$[1].nome", is("VR 3")));
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

    @Test
    @SneakyThrows
    public void atualizarSituacaoUsuarioBloqueado_deveAcessarService_seAutorizado() {
        mvc.perform(get("/api/usuarios/alterar-situacao-usuario-bloqueado/{usuarioId}", 123)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(deslogarUsuarioPorExcessoDeUsoService, times(1)).atualizarSituacaoUsuarioBloqueado(eq(123));
    }

    @Test
    @SneakyThrows
    public void atualizarSituacaoUsuarioBloqueado_naoDeveAcessarService_seNaoAutorizado() {
        mvc.perform(get("/api/usuarios/alterar-situacao-usuario-bloqueado/{usuarioId}", 123)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(deslogarUsuarioPorExcessoDeUsoService, never()).atualizarSituacaoUsuarioBloqueado(any(Integer.class));
    }

    @Test
    @SneakyThrows
    public void getTiposCanal_deveRetornarStatusOkEosCanais_quandoValido() {
        mvc.perform(get("/api/usuarios/tipos-canal")
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(5)))
            .andExpect(jsonPath("$[0].value", is(ETipoCanal.PAP.toString())))
            .andExpect(jsonPath("$[0].label", is(ETipoCanal.PAP.getDescricao().toUpperCase())))
            .andExpect(jsonPath("$[1].value", is(ETipoCanal.PAP_PME.toString())))
            .andExpect(jsonPath("$[1].label", is(ETipoCanal.PAP_PME.getDescricao().toUpperCase())))
            .andExpect(jsonPath("$[2].value", is(ETipoCanal.PAP_PREMIUM.toString())))
            .andExpect(jsonPath("$[2].label", is(ETipoCanal.PAP_PREMIUM.getDescricao().toUpperCase())))
            .andExpect(jsonPath("$[3].value", is(ETipoCanal.INSIDE_SALES_PME.toString())))
            .andExpect(jsonPath("$[3].label", is(ETipoCanal.INSIDE_SALES_PME.getDescricao().toUpperCase())))
            .andExpect(jsonPath("$[4].value", is(ETipoCanal.PAP_CONDOMINIO.toString())))
            .andExpect(jsonPath("$[4].label", is(ETipoCanal.PAP_CONDOMINIO.getDescricao().toUpperCase())));
    }

    @Test
    @SneakyThrows
    public void buscarSelectUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(USUARIOS_ENDPOINT + "/permitidos/select/por-filtros")
                .param("codigosCargos", "SUPERVISOR_OPERACAO,ASSISTENTE_OPERACAO")
                .header("Authorization", "")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(usuarioService, never()).buscarUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros(any());
    }

    @Test
    @SneakyThrows
    public void buscarSelectUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros_deveRetornarOk_quandoFiltrosObrigatoriosInformados() {
        mvc.perform(get(USUARIOS_ENDPOINT + "/permitidos/select/por-filtros")
                .param("codigosCargos", "SUPERVISOR_OPERACAO,ASSISTENTE_OPERACAO")
                .param("canal", "D2D_PROPRIO")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService, times(1))
            .buscarUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros(
                eq(UsuarioFiltros.builder()
                    .codigosCargos(List.of(SUPERVISOR_OPERACAO, ASSISTENTE_OPERACAO))
                    .canal(ECanal.D2D_PROPRIO)
                    .build()));
    }

    @Test
    @SneakyThrows
    public void getUsuariosOperacaoCanalAa_deveRetornarOk_seUsuarioAutenticado() {
        var codigoNivel = CodigoNivel.OPERACAO;
        mvc.perform(get(USUARIOS_ENDPOINT + "/nivel/canal")
                .param("codigoNivel", "OPERACAO")
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());

        verify(usuarioService, times(1)).getUsuariosOperacaoCanalAa(eq(codigoNivel));
    }

    @Test
    @SneakyThrows
    public void getUsuariosOperacaoCanalAa_deveRetornarUnauthorized_seUsuarioNaoAutenticado() {
        mvc.perform(get(USUARIOS_ENDPOINT + "/nivel/canal")
                .param("codigoCargos", "ADMINISTRADOR, GERENTE_OPERACAO"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getUsuariosAtivosByIds_deveRetornarListaIdsAtivos_quandoSolicitado() throws Exception {
        var listaIds = List.of(101, 104, 105);

        mvc.perform(post(USUARIOS_ENDPOINT + "/ativos")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(listaIds)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]", is(101)))
            .andExpect(jsonPath("$[1]", is(104)));
    }

    @Test
    public void getUsuariosAtivosByIds_deveRetornarUnauthorized_seUsuarioNaoAutenticado() throws Exception {
        var listaIds = List.of(101, 104, 105);

        mvc.perform(post(USUARIOS_ENDPOINT + "/ativos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(listaIds)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void findByUsuarioId_deveRetornarOk_quandoUsuarioExistir() {
        mvc.perform(get("/api/usuarios/{usuarioId}/subcanal/nivel", 100)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(100)))
            .andExpect(jsonPath("$.nome", is("ADMIN")))
            .andExpect(jsonPath("$.nivel", is("XBRAIN")))
            .andExpect(jsonPath("$.subCanais[0].id", is(1)))
            .andExpect(jsonPath("$.subCanais[0].codigo", is("PAP")))
            .andExpect(jsonPath("$.subCanais[0].nome", is("PAP")))
            .andExpect(jsonPath("$.subCanais[0].situacao", is("A")));

        verify(usuarioService, times(1)).findByUsuarioId(eq(100));
    }

    @Test
    @SneakyThrows
    public void findByUsuarioId_deveRetornarNotFound_quandoUsuarioNaoExistir() {
        mvc.perform(get("/api/usuarios/{usuarioId}/subcanal/nivel", 500)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$[0].message", is("O usuário " + 500 + " não foi encontrado.")));

        verify(usuarioService, times(1)).findByUsuarioId(eq(500));
    }

    @Test
    @SneakyThrows
    public void getUsuariosById_deveRetornarListaDeUsuarioResponse_quandoUsuariosCadastrados() {
        mvc.perform(post(USUARIOS_ENDPOINT + "/buscar-todos")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .content(String.valueOf(List.of(100, 101)))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(100)))
            .andExpect(jsonPath("$[0].nome", is("ADMIN")))
            .andExpect(jsonPath("$[0].codigoNivel", is("XBRAIN")))
            .andExpect(jsonPath("$[0].subCanais[0].id", is(1)))
            .andExpect(jsonPath("$[0].subCanais[0].codigo", is("PAP")))
            .andExpect(jsonPath("$[0].subCanais[0].nome", is("PAP")))
            .andExpect(jsonPath("$[1].id", is(101)))
            .andExpect(jsonPath("$[1].nome", is("HELPDESK")))
            .andExpect(jsonPath("$[1].codigoNivel", is("XBRAIN")))
            .andExpect(jsonPath("$[1].subCanais[0].id", is(1)))
            .andExpect(jsonPath("$[1].subCanais[0].codigo", is("PAP")))
            .andExpect(jsonPath("$[1].subCanais[0].nome", is("PAP")));

        verify(usuarioService, times(1)).getUsuariosByIdsTodasSituacoes(List.of(100, 101));
    }

    @Test
    @SneakyThrows
    public void getUsuariosById_deveRetornarListaVaziaDeUsuarioResponse_quandoUsuariosNaoCadastrados() {
        mvc.perform(post(USUARIOS_ENDPOINT + "/buscar-todos")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .content(String.valueOf(List.of(1, 2)))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(usuarioService, times(1)).getUsuariosByIdsTodasSituacoes(List.of(1, 2));
    }

    @Test
    @SneakyThrows
    public void getUsuariosById_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(post(USUARIOS_ENDPOINT + "/buscar-todos")
                .content(String.valueOf(List.of(1, 2)))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(usuarioService, never()).getUsuariosByIdsTodasSituacoes(any());
    }

    @Test
    @SneakyThrows
    public void findByCpf_deveRetornarOk_quandoUsuarioExistir() {
        mvc.perform(get(USUARIOS_ENDPOINT + "/cpf")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .param("cpf", "38957979875"))
            .andExpect(status().isOk());

        verify(usuarioService, times(1)).findByCpf(eq("38957979875"));
    }

    @Test
    @SneakyThrows
    public void findByCpf_naoDeveRetornarNotFound_quandoUsuarioNaoExistir() {
        mvc.perform(get(USUARIOS_ENDPOINT + "/cpf")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .param("cpf", "00000000000"))
            .andExpect(status().isOk());

        verify(usuarioService, times(1)).findByCpf(eq("00000000000"));
    }

    @Test
    @SneakyThrows
    public void findUsuarioInsideSalesByCpf_deveRetornarOk_quandoUsuarioExistir() {
        mvc.perform(get(USUARIOS_ENDPOINT + "/d2d")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .param("cpf", "38957979875"))
            .andExpect(status().isOk());

        verify(usuarioService, times(1)).findUsuarioD2dByCpf(eq("38957979875"));
    }

    @Test
    @SneakyThrows
    public void findUsuarioInsideSalesByCpf_naoDeveRetornarNotFound_quandoUsuarioNaoExistir() {
        mvc.perform(get(USUARIOS_ENDPOINT + "/d2d")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .param("cpf", "00000000000"))
            .andExpect(status().isOk());

        verify(usuarioService, times(1)).findUsuarioD2dByCpf(eq("00000000000"));
    }
}
