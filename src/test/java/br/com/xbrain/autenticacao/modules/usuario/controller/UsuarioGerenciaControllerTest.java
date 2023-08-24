package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.client.AgenteAutorizadoNovoClient;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.service.FileService;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendasUsuarioService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoClient;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioClientService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import helpers.Usuarios;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.EMPRESARIAL;
import static br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeederMso.RESIDENCIAL;
import static com.google.common.io.ByteStreams.toByteArray;
import static helpers.TestsHelper.*;
import static helpers.Usuarios.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.thymeleaf.util.StringUtils.concat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("classpath:/tests_database.sql")
public class UsuarioGerenciaControllerTest {

    private static final int ID_USUARIO_HELPDESK = 101;
    private static final int ID_USUARIO_VENDEDOR = 430;
    private static final String API_URI = "/api/usuarios/gerencia";
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private EntityManager entityManager;
    @SpyBean
    private UsuarioService usuarioService;
    @MockBean
    private EmailService emailService;
    @MockBean
    private FileService fileService;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @MockBean
    private EquipeVendasUsuarioService equipeVendasUsuarioService;
    @MockBean
    private AgenteAutorizadoClient agenteAutorizadoClient;
    @MockBean
    private AgenteAutorizadoNovoClient agenteAutorizadoNovoClient;
    @MockBean
    private SiteService siteService;
    @MockBean
    private UsuarioClientService usuarioClientService;

    @Test
    public void getAll_deveRetornarUnauthorized_quandoNaoInformarAToken() throws Exception {
        mvc.perform(get(API_URI)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void save_deveDarUnauthorized_quandoUsuarioNaoTiverPermissao() {
        mvc.perform(post(API_URI)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void save_deveRetornarForbidden_quandoNaoTiverPermissaoParaGerenciaDeUsuario() {
        mvc.perform(post(API_URI)
                .header("Authorization", getAccessToken(mvc, HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void alterar_deveRetornarErro_quandoUsuarioPertenceEquipe() {
        when(equipeVendasUsuarioService.buscarUsuarioEquipeVendasPorId(anyInt())).thenReturn(List.of(1));
        mvc.perform(put(API_URI)
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioParaAtualizacao("Pedro", 430, 10, 3, "731.407.220-52"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].message", is("Usuário já está cadastrado em outra equipe")));
    }

    @Test
    @SneakyThrows
    public void alterar_deveRetornarOk_quandoUsuarioPossuiDepartamentoNaoVerificado() {
        when(equipeVendasUsuarioService.buscarUsuarioEquipeVendasPorId(anyInt())).thenReturn(List.of(1));
        mvc.perform(put(API_URI)
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioParaAtualizacao("Pedro", 432, 10, 6, "132.355.930-20"))))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void alterar_deveRetornarOk_quandoUsuarioNaoPertenceAEquipe() {
        when(equipeVendasUsuarioService.buscarUsuarioEquipeVendasPorId(anyInt())).thenReturn(List.of(1));
        mvc.perform(put(API_URI)
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioParaAtualizacao("Pedro", 431, 10, 3, "122.861.350-88"))))
            .andExpect(status().isOk());
    }

    @Test
    public void getAll_deveRetornarForbidden_quandoNaoTiverPermissaoParaGerenciaDeUsuario() throws Exception {
        mvc.perform(get(API_URI)
                .header("Authorization", getAccessToken(mvc, HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getById_deveRetornarOUsuario_quandoInformadoOId() throws Exception {
        mvc.perform(get(concat(API_URI, "/", ID_USUARIO_HELPDESK))
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(ID_USUARIO_HELPDESK)))
            .andExpect(jsonPath("$.nome", is("HELPDESK")))
            .andExpect(jsonPath("$.nivelId", notNullValue()));
    }

    @Test
    public void getAll_deveFiltrarPorEmail_quandoOFiltroForPassado() throws Exception {
        mvc.perform(get(API_URI + "?email=HELPDESK@XBRAIN.COM.BR")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(ID_USUARIO_HELPDESK)))
            .andExpect(jsonPath("$.nome", is("HELPDESK")));
    }

    @Test
    public void getAll_deveRetornarTodosOsUsuarios_quandoForAdmin() throws Exception {
        mvc.perform(get(API_URI)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(10)))
            .andExpect(jsonPath("$.content[0].nome", is("ADMIN")));
    }

    @Test
    public void getAll_deveNaoRetornarOsUsuariosXbrain_quandoForNivelMso() throws Exception {
        mvc.perform(get(API_URI)
                .header("Authorization", getAccessToken(mvc, MSO_ANALISTAADM_CLAROMOVEL_PESSOAL))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(10)))
            .andExpect(jsonPath("$.content[0].nome", is("Supervisor Operação")))
            .andExpect(jsonPath("$.content[0].tiposFeeder", empty()))
            .andExpect(jsonPath("$.content[1].nome", is("operacao_gerente_comercial")))
            .andExpect(jsonPath("$.content[1].tiposFeeder", empty()))
            .andExpect(jsonPath("$.content[2].nome", is("Assistente Operação")))
            .andExpect(jsonPath("$.content[2].tiposFeeder", empty()))
            .andExpect(jsonPath("$.content[3].nome", is("Vendedor Operação")))
            .andExpect(jsonPath("$.content[3].tiposFeeder", empty()))
            .andExpect(jsonPath("$.content[4].nome", is("Agente Autorizado Aprovação MSO Novos Cadastros")))
            .andExpect(jsonPath("$.content[4].tiposFeeder", empty()))
            .andExpect(jsonPath("$.content[5].nome", is("Operacao Supervisor NET")))
            .andExpect(jsonPath("$.content[5].tiposFeeder", empty()))
            .andExpect(jsonPath("$.content[6].nome", is("Mso Analista Adm Claro Pessoal")))
            .andExpect(jsonPath("$.content[6].tiposFeeder", containsInAnyOrder(EMPRESARIAL.name(), RESIDENCIAL.name())))
            .andExpect(jsonPath("$.content[7].nome", is("Operacao Supervisor")))
            .andExpect(jsonPath("$.content[7].tiposFeeder", empty()))
            .andExpect(jsonPath("$.content[8].nome", is("Operacao Gerente")))
            .andExpect(jsonPath("$.content[8].tiposFeeder", empty()))
            .andExpect(jsonPath("$.content[9].nome", is("Operacao Vendedor")))
            .andExpect(jsonPath("$.content[9].tiposFeeder", empty()));
    }

    @Test
    public void getAll_deveRetornarUsuarios_quandoFiltroForComOrganizacaoEmpresaId() throws Exception {
        mvc.perform(get(API_URI + "?organizacaoId=2")
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(3)))
            .andExpect(jsonPath("$.content[0].nome", is("HELPDESK")))
            .andExpect(jsonPath("$.content[0].email", is("HELPDESK@XBRAIN.COM.BR")))
            .andExpect(jsonPath("$.content[0].tiposFeeder", empty()))
            .andExpect(jsonPath("$.content[1].nome", is("operacao_gerente_comercial")))
            .andExpect(jsonPath("$.content[1].email", is("operacao_gerente_comercial@net.com.br")))
            .andExpect(jsonPath("$.content[1].tiposFeeder", empty()))
            .andExpect(jsonPath("$.content[2].nome", is("Mso Analista Adm Claro Pessoal")))
            .andExpect(jsonPath("$.content[2].email",
                is("MSO_ANALISTAADM_CLAROMOVEL_PESSOAL@NET.COM.BR")))
            .andExpect(jsonPath("$.content[2].tiposFeeder", containsInAnyOrder(EMPRESARIAL.name(), RESIDENCIAL.name())));
    }

    @Test
    public void getUsuariosCargoSuperior_deveRetornarTodos_porCargoSuperior() throws Exception {
        mvc.perform(post(API_URI + "/cargo-superior/4")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(
                    UsuarioCargoSuperiorPost
                        .builder()
                        .cidadeIds(List.of(1, 5578))
                        .build())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[0].nome", is("Agente Autorizado Aprovação MSO Novos Cadastros")))
            .andExpect(jsonPath("$.[1].nome", is("operacao_gerente_comercial")));
    }

    @Test
    @SneakyThrows
    public void getUsuariosCargoSuperiorByCanal_deveRetornarTodos_porCargoSuperiorEFiltroOrganizacaoId() {
        mvc.perform(post(API_URI + "/cargo-superior/501/INTERNET")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(
                    UsuarioCargoSuperiorPost
                        .builder()
                        .organizacaoId(43)
                        .build())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].nome", is("INTERNET GERENTE")));
    }

    @Test
    @SneakyThrows
    public void getUsuariosCargoSuperiorByCanal_naoDeveRetornar_quandoNaoLocalizarAtravesDeOrganizacaoId() {
        mvc.perform(post(API_URI + "/cargo-superior/501/INTERNET")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(
                    UsuarioCargoSuperiorPost
                        .builder()
                        .organizacaoId(12399)
                        .build())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void listarUsuario_deveRetornarTodosByCnpjAa_quandoFiltrar() throws Exception {
        mockResponseAgenteAutorizado();
        mockResponseUsuariosAgenteAutorizado();

        mvc.perform(get(API_URI + "?cnpjAa=09.489.617/0001-97")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(4)))
            .andExpect(jsonPath("$.content[0].nome", is("ADMIN")));
    }

    @Test
    public void listarUsuario_deveRetornarTodosByCnpjAa_quandoFiltrarPorAtivos() throws Exception {
        mockResponseAgenteAutorizado();
        mockResponseUsuariosAgenteAutorizado();

        mvc.perform(get(API_URI)
                .param("cnpjAa", "09.489.617/0001-97")
                .param("situacoes", "A")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(3)))
            .andExpect(jsonPath("$.content[1].nome", is("HELPDESK")))
            .andExpect(jsonPath("$.content[1].situacao", is("A")));
    }

    @Test
    public void listarUsuario_deveRetornarTodosByCnpjAa_quandoFiltrarPorInativos() throws Exception {
        mockResponseAgenteAutorizado();
        mockResponseUsuariosAgenteAutorizado();

        mvc.perform(get(API_URI)
                .param("cnpjAa", "09.489.617/0001-97")
                .param("situacoes", "I")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void deveFiltrarPorNome() throws Exception {
        mvc.perform(get(API_URI + "?nome=ADMIN")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void filtrarUser_deveFiltrarPorInativo_quandoSituacaoForInativo() throws Exception {
        mvc.perform(get(API_URI)
                .param("situacoes", "I")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].nome", is("INATIVO")));
    }

    @Test
    public void filtrarUser_deveFiltrarPorRealocado_quandoSituacaoForRealocado() throws Exception {
        mvc.perform(get(API_URI)
                .param("situacoes", "R")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[0].nome", is("REALOCADO")));
    }

    @Test
    public void deveValidarOsCamposNulosNoCadastro() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(new UsuarioDto()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void deveValidarOCampo_throwException_quandoUnidadeNegocioVazio() throws Exception {
        var request = umUsuario("Big");
        request.setUnidadesNegociosId(List.of());
        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(request))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[*].message", containsInAnyOrder("O campo unidadesNegociosId é obrigatório.")));
    }

    @Test
    public void deveValidarOCampo_throwException_quandoEmpresaVazio() throws Exception {
        var request = umUsuario("Big");
        request.setEmpresasId(List.of());
        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(request))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[*].message", containsInAnyOrder("O campo empresasId é obrigatório.")));
    }

    @Test
    public void deveSalvarSemFoto() throws Exception {
        UsuarioDto usuario = umUsuario("JOAO");
        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(usuario))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());

        verify(emailService, times(1)).enviarEmailTemplate(any(), any(), any(), any());
        verify(fileService, times(0)).uploadFotoUsuario(any(), any());

        List<Usuario> usuarios = Lists.newArrayList(
            repository.findAll(new UsuarioPredicate().comNome(usuario.getNome()).build()));

        assertEquals(usuarios.get(0).getNome(), usuario.getNome());
        assertEquals(usuarios.get(0).getCpf(), "09723864592");
        assertEquals(usuarios.get(0).getCanais(), Sets.newHashSet(ECanal.AGENTE_AUTORIZADO, ECanal.D2D_PROPRIO));
    }

    @Test
    public void deveSalvarComFoto() throws Exception {
        UsuarioDto usuario = umUsuario("JOAO");
        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umFileFoto())
                .file(umUsuario(usuario))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());

        verify(emailService, times(1)).enviarEmailTemplate(any(), any(), any(), any());
        verify(fileService, times(1)).uploadFotoUsuario(any(), any());

        List<Usuario> usuarios = Lists.newArrayList(
            repository.findAll(new UsuarioPredicate().comNome(usuario.getNome()).build()));

        assertEquals(usuarios.get(0).getNome(), usuario.getNome());
        assertEquals(usuarios.get(0).getCpf(), "09723864592");
        assertEquals(usuarios.get(0).getCanais(), Sets.newHashSet(ECanal.AGENTE_AUTORIZADO, ECanal.D2D_PROPRIO));
    }

    @Test
    public void deveSalvarAConfiguracaoDoUsuario() throws Exception {
        UsuarioConfiguracaoSaveDto dto = new UsuarioConfiguracaoSaveDto();
        dto.setUsuarioId(ID_USUARIO_HELPDESK);
        dto.setRamal(1234);
        mvc.perform(post(API_URI + "/configuracao")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
            .andExpect(status().isOk());
    }

    @Test
    public void deveAlterarAConfiguracaoDoUsuario() throws Exception {
        UsuarioConfiguracaoSaveDto dto = new UsuarioConfiguracaoSaveDto();
        dto.setUsuarioId(100);
        dto.setRamal(6666);
        mvc.perform(post(API_URI + "/configuracao")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
            .andExpect(status().isOk());
        Usuario usuario = repository.findComplete(100).orElse(new Usuario());
        Assert.assertEquals(usuario.getConfiguracao().getRamal(), Integer.valueOf(6666));
    }

    @Test
    public void deveEditarSemFoto() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(umUsuarioParaEditar()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());
        Usuario usuario = repository.findOne(ID_USUARIO_HELPDESK);
        Assert.assertEquals(usuario.getNome(), "JOAOZINHO");
    }

    @Test
    public void deveEditarComFoto() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umFileFoto())
                .file(umUsuario(umUsuarioParaEditar()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());
        Usuario usuario = repository.findOne(ID_USUARIO_HELPDESK);
        Assert.assertEquals(usuario.getNome(), "JOAOZINHO");
    }

    @Test
    public void deveInativarUmUsuario() throws Exception {
        doNothing().when(usuarioClientService).alterarSituacao(anyInt());

        mvc.perform(post(API_URI + "/inativar")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioParaInativar())))
            .andExpect(status().isOk());
        Usuario usuario = repository.findOne(ID_USUARIO_HELPDESK);
        Assert.assertEquals(usuario.getSituacao(), ESituacao.I);
    }

    @Test
    public void deveAlterarASenhaDeUmUsuarioEEnviarPorEmail() throws Exception {
        mvc.perform(put(API_URI + "/100/senha")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(emailService, times(1)).enviarEmailTemplate(any(), any(), any(), any());
    }

    @Test
    public void deveRetornarAsPermissoesDoUsuario() throws Exception {
        mvc.perform(get(API_URI + "/100/permissoes")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.permissoesCargoDepartamento", is(not(empty()))))
            .andExpect(jsonPath("$.permissoesEspeciais", hasSize(3)));
    }

    @Test
    public void deveRetornarAsCidadesAtreladasAoUsuario() throws Exception {
        mvc.perform(get(API_URI + "/100/cidades")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void deveAlterarOEmailDoUsuario() throws Exception {
        mvc.perform(put(API_URI + "/acesso/email")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umRequestDadosAcessoEmail())))
            .andExpect(status().isOk());
        verify(emailService, times(2)).enviarEmailTemplate(any(), any(), any(), any());
    }

    @Test
    public void deveNaoTrocarOEmailDoUsuarioQuandoForDiferenteDoDaBase() throws Exception {
        UsuarioDadosAcessoRequest dto = umRequestDadosAcessoSenha();
        dto.setEmailAtual("EMAILERRADO@XBRAIN.COM.BR");
        mvc.perform(put(API_URI + "/acesso/email")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void deveAlterarASenhaDoUsuario() throws Exception {
        mvc.perform(put(API_URI + "/acesso/senha")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umRequestDadosAcessoSenha())))
            .andExpect(status().isOk());
        verify(emailService, times(1)).enviarEmailTemplate(any(), any(), any(), any());
    }

    @Test
    public void deveAlterarASenhaDoUsuarioIgnorandoSenhaAtual() throws Exception {
        UsuarioDadosAcessoRequest objTest = umRequestDadosAcessoSenha();
        objTest.setIgnorarSenhaAtual(Boolean.TRUE);
        objTest.setSenhaAtual("");

        mvc.perform(put("/api/usuarios/gerencia/acesso/senha")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(objTest)))
            .andExpect(status().isOk());
        verify(emailService, times(1)).enviarEmailTemplate(any(), any(), any(), any());
    }

    @Test
    public void deveNaoTrocarASenhaDoUsuarioQuandoForDiferenteDoDaBase() throws Exception {
        UsuarioDadosAcessoRequest dto = umRequestDadosAcessoSenha();
        dto.setSenhaAtual("SENHAINCORRETA");
        mvc.perform(put(API_URI + "/acesso/senha")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void deveRetornarOSuperiorDoUsuario() throws Exception {
        mvc.perform(get(API_URI + "/101/supervisor")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(104)))
            .andExpect(jsonPath("$.nome", is("operacao_gerente_comercial")))
            .andExpect(jsonPath("$.email", is("operacao_gerente_comercial@net.com.br")));
    }

    @Test
    public void deveRetornarOSuperioresDoUsuario() throws Exception {
        mvc.perform(get(API_URI + "/101/supervisores")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void getCsv_CsvFormatadoCorretamente_QuandoRetornadoDoisUsuarios() throws Exception {
        doReturn(doisUsuariosCsvResponse()).when(usuarioService).getAllForCsv(any(UsuarioFiltros.class));
        String csv = mvc.perform(get(API_URI + "/csv")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv; charset=UTF-8"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(
            "\uFEFFCODIGO;NOME;EMAIL;TELEFONE;CPF;CARGO;DEPARTAMENTO;UNIDADE NEGOCIO;EMPRESA;SITUACAO;"
                + "DATA ULTIMO ACESSO;LOGIN NETSALES;NIVEL;RAZAO SOCIAL;CNPJ;ORGANIZACAO EMPRESA;CANAL;HIERARQUIA\n"
                + "1;Usuario Csv;usuario_csv@xbrain.com.br;(43) 2323-1782;754.000.720-62;Vendedor;Comercial;"
                + "X-Brain. Claro Residencial;X-Brain. Claro TV;A;;;;;;;;\n"
                + "2;Usuario Teste;usuario_teste@xbrain.com.br;(43) 4575-5878;048.038.280-83;Vendedor;Comercial;"
                + "X-Brain. Residencial e Combos;X-Brain. Claro TV;A;;;;;;;;", csv);
    }

    @Test
    public void getCsv_CsvFormatadoCorretamente_QuandoUsuarioNaoPossuirEmpresaEUnidadeNegocio() throws Exception {
        doReturn(doisUsuariosCsvResponseSemEmpresasEUnidadesNegocios())
            .when(usuarioService).getAllForCsv(any(UsuarioFiltros.class));

        String csv = mvc.perform(get(API_URI + "/csv")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv; charset=UTF-8"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals("\uFEFFCODIGO;NOME;EMAIL;TELEFONE;CPF;CARGO;DEPARTAMENTO;UNIDADE NEGOCIO;EMPRESA;SITUACAO;"
            + "DATA ULTIMO ACESSO;LOGIN NETSALES;NIVEL;RAZAO SOCIAL;CNPJ;ORGANIZACAO EMPRESA;CANAL;HIERARQUIA\n"
            + "1;Usuario Csv;usuario_csv@xbrain.com.br;(43) 2323-1782;754.000.720-62;Vendedor;Comercial;"
            + ";;A;;;;;;;;\n"
            + "2;Usuario Teste;usuario_teste@xbrain.com.br;(43) 4575-5878;048.038.280-83;Vendedor;Comercial;"
            + ";;A;;;;;;;;", csv);
    }

    @Test
    public void validarSeUsuarioNovoCadastro_deveRetornarTrue_quandoEmailECpfNaoExistem() throws Exception {

        mvc.perform(get(API_URI + "/existir/usuario")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .param("email", "JOHN@GMAIL.COM")
                .param("cpf", "48503182076"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(Boolean.TRUE)));
    }

    @Test
    public void validarSeUsuarioNovoCadastro_deveThrowValidacaoException_quandoEmailCadastrado() throws Exception {

        mvc.perform(get(API_URI + "/existir/usuario")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .param("cpf", "48503182076")
                .param("email", "HELPDESK@XBRAIN.COM.BR"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "Email já cadastrado.")));
    }

    @Test
    public void validarSeUsuarioNovoCadastro_deveThrowValidacaoException_quandoCpfCadastrado() throws Exception {

        mvc.perform(get(API_URI + "/existir/usuario")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .param("cpf", "99898798782")
                .param("email", "JOHN@GMAIL.COM"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "CPF já cadastrado.")));
    }

    @Test
    public void getAllXbrainMsoAtivos_deveRetornarUnauthorized_quandoNaoInformarToken() throws Exception {
        mvc.perform(get(API_URI + "/chamados/usuarios-redirecionamento/2")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllXbrainMsoAtivos_deveRetornarOk_quandoUsuarioForAutorizado() throws Exception {
        mvc.perform(get(API_URI + "/chamados/usuarios-redirecionamento/2")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    private UsuarioDadosAcessoRequest umRequestDadosAcessoEmail() {
        UsuarioDadosAcessoRequest dto = new UsuarioDadosAcessoRequest();
        dto.setUsuarioId(101);
        dto.setEmailAtual("HELPDESK@XBRAIN.COM.BR");
        dto.setEmailNovo("NOVOEMAIL@XBRAIN.COM.BR");
        return dto;
    }

    private UsuarioDadosAcessoRequest umRequestDadosAcessoSenha() {
        UsuarioDadosAcessoRequest dto = new UsuarioDadosAcessoRequest();
        dto.setUsuarioId(101);
        dto.setAlterarSenha(Eboolean.V);
        dto.setSenhaAtual("123456");
        dto.setSenhaNova("654321");
        dto.setIgnorarSenhaAtual(Boolean.FALSE);
        return dto;
    }

    private UsuarioInativacaoDto umUsuarioParaInativar() {
        UsuarioInativacaoDto dto = new UsuarioInativacaoDto();
        dto.setIdUsuario(ID_USUARIO_HELPDESK);
        dto.setObservacao("Teste inativação");
        dto.setCodigoMotivoInativacao(CodigoMotivoInativacao.FERIAS);
        return dto;
    }

    private UsuarioDto umUsuarioParaEditar() {
        Usuario usuario = repository.findComplete(ID_USUARIO_HELPDESK).get();
        usuario.forceLoad();
        usuario.setNome("JOAOZINHO");
        usuario.setLoginNetSales("MIDORIYA SHOUNEN");
        return UsuarioDto.of(usuario);
    }

    private UsuarioDto umUsuario(String nome) {
        UsuarioDto usuario = new UsuarioDto();
        usuario.setNome(nome);
        usuario.setCargoId(3);
        usuario.setDepartamentoId(1);
        usuario.setCpf("097.238.645-92");
        usuario.setUnidadesNegociosId(Arrays.asList(1));
        usuario.setEmpresasId(singletonList(4));
        usuario.setEmail("usuario@teste.com");
        usuario.setTelefone("43 995565661");
        usuario.setHierarquiasId(Arrays.asList(100));
        usuario.setCidadesId(Arrays.asList(736, 2921, 527));
        usuario.setLoginNetSales("MIDORIYA SHOUNEN");
        usuario.setCanais(Sets.newHashSet(ECanal.AGENTE_AUTORIZADO, ECanal.D2D_PROPRIO));
        return usuario;
    }

    private MockMultipartFile umUsuario(UsuarioDto usuario) throws Exception {
        byte[] json = convertObjectToJsonString(usuario).getBytes(StandardCharsets.UTF_8);
        return new MockMultipartFile("usuario", "json", "application/json", json);
    }

    private UsuarioDto umUsuarioParaAtualizacao(String nome, Integer id, Integer cargo, Integer departamento, String cpf) {
        UsuarioDto usuario = new UsuarioDto();
        usuario.setId(id);
        usuario.setNome(nome);
        usuario.setCargoId(cargo);
        usuario.setDepartamentoId(departamento);
        usuario.setCpf(cpf);
        usuario.setUnidadesNegociosId(Arrays.asList(1));
        usuario.setEmpresasId(singletonList(4));
        usuario.setEmail("usuario@teste.com");
        usuario.setTelefone("43 995565661");
        usuario.setHierarquiasId(Arrays.asList(100));
        usuario.setCidadesId(Arrays.asList(736, 2921, 527));
        usuario.setLoginNetSales("MIDORIYA SHOUNEN");
        usuario.setCanais(Sets.newHashSet(ECanal.D2D_PROPRIO));
        usuario.setSituacao(ESituacao.A);
        return usuario;
    }

    private void mockResponseAgenteAutorizado() {
        AgenteAutorizadoResponse response = AgenteAutorizadoResponse.builder()
            .id("100")
            .cnpj("09.489.617/0001-97")
            .build();

        when(agenteAutorizadoNovoClient.getAaByCpnj(Matchers.anyMap()))
            .thenReturn(response);
    }

    private void mockResponseUsuariosAgenteAutorizado() {
        List<UsuarioAgenteAutorizadoResponse> response = new ArrayList<>();
        response.add(new UsuarioAgenteAutorizadoResponse(100));
        response.add(new UsuarioAgenteAutorizadoResponse(101));
        response.add(new UsuarioAgenteAutorizadoResponse(104));
        response.add(new UsuarioAgenteAutorizadoResponse(105));

        when(agenteAutorizadoNovoClient.getUsuariosByAaId(Matchers.anyInt(), Matchers.anyBoolean()))
            .thenReturn(response);
    }

    private MockMultipartFile umFileFoto() throws Exception {
        byte[] bytes = toByteArray(getFileInputStream("foto_usuario/file.png"));
        return new MockMultipartFile("foto",
            LocalDateTime.now().toString().concat("file.png"),
            "image/png",
            bytes);
    }

    private InputStream getFileInputStream(String file) throws Exception {
        return new ByteArrayInputStream(
            Files.readAllBytes(Paths.get(
                getClass().getClassLoader().getResource(file)
                    .getPath())));
    }

    private List<UsuarioCsvResponse> doisUsuariosCsvResponse() {
        return asList(
            UsuarioCsvResponse.builder()
                .id(1)
                .nome("Usuario Csv")
                .email("usuario_csv@xbrain.com.br")
                .telefone("(43) 2323-1782")
                .cpf("75400072062")
                .cargo("Vendedor")
                .departamento("Comercial")
                .unidadesNegocios("X-Brain. Claro Residencial")
                .empresas("X-Brain. Claro TV")
                .situacao(ESituacao.A)
                .build(),
            UsuarioCsvResponse.builder()
                .id(2)
                .nome("Usuario Teste")
                .email("usuario_teste@xbrain.com.br")
                .telefone("(43) 4575-5878")
                .cpf("04803828083")
                .cargo("Vendedor")
                .departamento("Comercial")
                .unidadesNegocios("X-Brain. Residencial e Combos")
                .empresas("X-Brain. Claro TV")
                .situacao(ESituacao.A)
                .build()
        );
    }

    private List<UsuarioCsvResponse> doisUsuariosCsvResponseSemEmpresasEUnidadesNegocios() {
        return asList(
            UsuarioCsvResponse.builder()
                .id(1)
                .nome("Usuario Csv")
                .email("usuario_csv@xbrain.com.br")
                .telefone("(43) 2323-1782")
                .cpf("75400072062")
                .cargo("Vendedor")
                .departamento("Comercial")
                .situacao(ESituacao.A)
                .build(),
            UsuarioCsvResponse.builder()
                .id(2)
                .nome("Usuario Teste")
                .email("usuario_teste@xbrain.com.br")
                .telefone("(43) 4575-5878")
                .cpf("04803828083")
                .cargo("Vendedor")
                .departamento("Comercial")
                .situacao(ESituacao.A)
                .build()
        );
    }
}
