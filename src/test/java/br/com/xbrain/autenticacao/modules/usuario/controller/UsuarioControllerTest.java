package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioAtivacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioInativacaoDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import helpers.Usuarios;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

import java.time.LocalDateTime;

import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class UsuarioControllerTest {

    private static final int ID_USUARIO_HELPDESK = 101;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UsuarioRepository repository;

    @MockBean
    private AutenticacaoService autenticacaoService;

    @Before
    public void setup() {
        Mockito.when(autenticacaoService.getUsuarioId())
                .thenReturn(100);
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
                .andExpect(jsonPath("$", hasSize(3)));
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
        dto.setCodigoMotivoInativacao(CodigoMotivoInativacao.FERIAS);
        return dto;
    }
}
