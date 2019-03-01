package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.service.FileService;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoClient;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import helpers.Usuarios;
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

import static com.google.common.io.ByteStreams.toByteArray;
import static helpers.TestsHelper.*;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.HELP_DESK;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class UsuarioGerenciaControllerTest {

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
    private EquipeVendaService equipeVendaService;
    @MockBean
    private AgenteAutorizadoClient agenteAutorizadoClient;

    private static final int ID_USUARIO_HELPDESK = 101;

    @Test
    public void deveSolicitarAutenticacao() throws Exception {
        mvc.perform(get("/api/usuarios/gerencia")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deveTerPermissaoDeGerenciaDeUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/gerencia")
                .header("Authorization", getAccessToken(mvc, HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void somenteUsuariosComPermissaoDeGerenciaDeUsuariosPodeTerAcesso() throws Exception {
        mvc.perform(get("/api/usuarios")
                .header("Authorization", getAccessToken(mvc, Usuarios.MSO_ANALISTAADM_CLAROMOVEL_PESSOAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deveRetornarPorId() throws Exception {
        mvc.perform(get("/api/usuarios/gerencia/" + ID_USUARIO_HELPDESK)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ID_USUARIO_HELPDESK)))
                .andExpect(jsonPath("$.nome", is("HELPDESK")))
                .andExpect(jsonPath("$.nivelId", notNullValue()));
    }

    @Test
    public void deveRetornarPorEmail() throws Exception {
        mvc.perform(get("/api/usuarios/gerencia?email=HELPDESK@XBRAIN.COM.BR")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ID_USUARIO_HELPDESK)))
                .andExpect(jsonPath("$.nome", is("HELPDESK")));
    }

    @Test
    public void deveRetornarTodos() throws Exception {
        mvc.perform(get("/api/usuarios/gerencia")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(7)))
                .andExpect(jsonPath("$.content[0].nome", is("ADMIN")));
    }

    @Test
    public void getUsuariosCargoSuperior_deveRetornarTodos_porCargoSuperior() throws Exception {
        mvc.perform(get("/api/usuarios/gerencia/cargo-superior/4")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].nome", is("Agente Autorizado Aprovação MSO Novos Cadastros")));
    }

    @Test
    public void listarUsuario_deveRetornarTodosByCnpjAa_quandoFiltrar() throws Exception {
        mockResponseAgenteAutorizado();
        mockResponseUsuariosAgenteAutorizado();

        mvc.perform(get("/api/usuarios/gerencia?cnpjAa=09.489.617/0001-97")
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

        mvc.perform(get("/api/usuarios/gerencia?cnpjAa=09.489.617/0001-97&situacao=A")
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

        mvc.perform(get("/api/usuarios/gerencia?cnpjAa=09.489.617/0001-97&situacao=I")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void deveFiltrarPorNome() throws Exception {
        mvc.perform(get("/api/usuarios/gerencia?nome=ADMIN")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void deveFiltrarPorInativo() throws Exception {
        mvc.perform(get("/api/usuarios/gerencia?situacao=I")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome", is("INATIVO")));
    }

    @Test
    public void deveValidarOsCamposNulosNoCadastro() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/usuarios/gerencia")
                .file(umUsuario(new UsuarioDto()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(7)))
                .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                        "O campo nome é obrigatório.",
                        "O campo cpf é obrigatório.",
                        "O campo email é obrigatório.",
                        "O campo unidadesNegociosId é obrigatório.",
                        "O campo empresasId é obrigatório.",
                        "O campo cargoId é obrigatório.",
                        "O campo departamentoId é obrigatório.")));
    }

    @Test
    public void deveSalvarSemFoto() throws Exception {
        UsuarioDto usuario = umUsuario("JOAO");
        mvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/usuarios/gerencia")
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
                .fileUpload("/api/usuarios/gerencia")
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
        mvc.perform(post("/api/usuarios/gerencia/configuracao")
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
        mvc.perform(post("/api/usuarios/gerencia/configuracao")
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
                .fileUpload("/api/usuarios/gerencia")
                .file(umUsuario(umUsuarioParaEditar()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
                .andExpect(status().isOk());
        Usuario usuario = repository.findOne(ID_USUARIO_HELPDESK);
        Assert.assertEquals(usuario.getNome(), "JOAOZINHO");

        verify(equipeVendaService, times(0)).inativarUsuario(any());
    }

    @Test
    public void deveEditarComFoto() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/usuarios/gerencia")
                .file(umFileFoto())
                .file(umUsuario(umUsuarioParaEditar()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", getAccessToken(mvc, ADMIN)))
                .andExpect(status().isOk());
        Usuario usuario = repository.findOne(ID_USUARIO_HELPDESK);
        Assert.assertEquals(usuario.getNome(), "JOAOZINHO");

        verify(equipeVendaService, times(0)).inativarUsuario(any());
    }

    @Test
    public void deveInativarUmUsuario() throws Exception {
        mvc.perform(post("/api/usuarios/gerencia/inativar")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioParaInativar())))
                .andExpect(status().isOk());
        Usuario usuario = repository.findOne(ID_USUARIO_HELPDESK);
        Assert.assertEquals(usuario.getSituacao(), ESituacao.I);
    }

    @Test
    public void deveAlterarASenhaDeUmUsuarioEEnviarPorEmail() throws Exception {
        mvc.perform(put("/api/usuarios/gerencia/100/senha")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(emailService, times(1)).enviarEmailTemplate(any(), any(), any(), any());
    }

    @Test
    public void deveRetornarAsPermissoesDoUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/gerencia/100/permissoes")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissoesCargoDepartamento", hasSize(93)))
                .andExpect(jsonPath("$.permissoesEspeciais", hasSize(0)));
    }

    @Test
    public void deveRetornarAsCidadesAtreladasAoUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/gerencia/100/cidades")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void deveAlterarOEmailDoUsuario() throws Exception {
        mvc.perform(put("/api/usuarios/gerencia/acesso/email")
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
        mvc.perform(put("/api/usuarios/gerencia/acesso/email")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deveAlterarASenhaDoUsuario() throws Exception {
        mvc.perform(put("/api/usuarios/gerencia/acesso/senha")
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
        mvc.perform(put("/api/usuarios/gerencia/acesso/senha")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deveRetornarOSuperiorDoUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/gerencia/101/supervisor")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(104)))
                .andExpect(jsonPath("$.nome", is("operacao_gerente_comercial")))
                .andExpect(jsonPath("$.email", is("operacao_gerente_comercial@net.com.br")));
    }

    @Test
    public void deveRetornarOSuperioresDoUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/gerencia/101/supervisores")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void getCsv_CsvFormatadoCorretamente_QuandoRetornadoDoisUsuarios() throws Exception {
        doReturn(doisUsuariosCsvResponse()).when(usuarioService).getAllForCsv(any(UsuarioFiltros.class));
        String csv = mvc.perform(get("/api/usuarios/gerencia/csv")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv; charset=UTF-8"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(
                "\uFEFFCODIGO;NOME;EMAIL;TELEFONE;CPF;CARGO;DEPARTAMENTO;UNIDADE NEGOCIO;EMPRESA;SITUACAO\n"
                        + "1;Usuario Csv;usuario_csv@xbrain.com.br;(43) 2323-1782;754.000.720-62;Vendedor;Comercial;"
                        + "X-Brain. Claro Residencial;X-Brain. Claro TV;A\n"
                        + "2;Usuario Teste;usuario_teste@xbrain.com.br;(43) 4575-5878;048.038.280-83;Vendedor;Comercial;"
                        + "X-Brain. Residencial e Combos;X-Brain. Claro TV;A", csv);
    }

    @Test
    public void getCsv_CsvFormatadoCorretamente_QuandoUsuarioNaoPossuirEmpresaEUnidadeNegocio() throws Exception {
        doReturn(doisUsuariosCsvResponseSemEmpresasEUnidadesNegocios())
                .when(usuarioService).getAllForCsv(any(UsuarioFiltros.class));
        String csv = mvc.perform(get("/api/usuarios/gerencia/csv")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv; charset=UTF-8"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(
                "\uFEFFCODIGO;NOME;EMAIL;TELEFONE;CPF;CARGO;DEPARTAMENTO;UNIDADE NEGOCIO;EMPRESA;SITUACAO\n"
                        + "1;Usuario Csv;usuario_csv@xbrain.com.br;(43) 2323-1782;754.000.720-62;Vendedor;Comercial;"
                        + ";;A\n"
                        + "2;Usuario Teste;usuario_teste@xbrain.com.br;(43) 4575-5878;048.038.280-83;Vendedor;Comercial;"
                        + ";;A", csv);
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

    private UsuarioAtivacaoDto umUsuarioParaAtivar() {
        UsuarioAtivacaoDto dto = new UsuarioAtivacaoDto();
        dto.setIdUsuario(ID_USUARIO_HELPDESK);
        dto.setObservacao("Teste ativação");
        return dto;
    }

    private UsuarioInativacaoDto umUsuarioParaInativar() {
        UsuarioInativacaoDto dto = new UsuarioInativacaoDto();
        dto.setDataCadastro(LocalDateTime.now());
        dto.setIdUsuario(ID_USUARIO_HELPDESK);
        dto.setObservacao("Teste inativação");
        dto.setIdMotivoInativacao(1);
        return dto;
    }

    private UsuarioDto umUsuarioParaEditar() {
        Usuario usuario = repository.findComplete(ID_USUARIO_HELPDESK).get();
        usuario.forceLoad();
        usuario.setNome("JOAOZINHO");
        return UsuarioDto.convertTo(usuario);
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
        usuario.setCanais(Sets.newHashSet(ECanal.AGENTE_AUTORIZADO, ECanal.D2D_PROPRIO));
        return usuario;
    }

    private MockMultipartFile umUsuario(UsuarioDto usuario) throws Exception {
        byte[] json = convertObjectToJsonString(usuario).getBytes(StandardCharsets.UTF_8);
        return new MockMultipartFile("usuario", "json", "application/json", json);
    }

    private void mockResponseAgenteAutorizado() {
        AgenteAutorizadoResponse response = new AgenteAutorizadoResponse();
        response.setId("100");
        response.setCnpj("09.489.617/0001-97");

        when(agenteAutorizadoClient.getAaByCpnj(Matchers.anyMap()))
                .thenReturn(response);
    }

    private void mockResponseUsuariosAgenteAutorizado() {
        List<UsuarioAgenteAutorizadoResponse> response = new ArrayList<>();
        response.add(new UsuarioAgenteAutorizadoResponse(100));
        response.add(new UsuarioAgenteAutorizadoResponse(101));
        response.add(new UsuarioAgenteAutorizadoResponse(104));
        response.add(new UsuarioAgenteAutorizadoResponse(105));

        when(agenteAutorizadoClient.getUsuariosByAaId(Matchers.anyInt(), Matchers.anyBoolean()))
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
