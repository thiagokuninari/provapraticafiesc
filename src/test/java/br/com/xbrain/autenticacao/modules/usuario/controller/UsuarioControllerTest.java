package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.google.common.collect.Lists;
import javax.persistence.EntityManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.HELP_DESK;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
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

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UsuarioService usuarioService;

    private static final int ID_USUARIO_HELPDESK = 101;

    @Test
    public void deveSolicitarAutenticacao() throws Exception {
        mvc.perform(get("/api/usuarios")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deveTerPermissaoDeGerenciaDeUsuario() throws Exception {
        mvc.perform(get("/api/usuarios")
                .header("Authorization", getAccessToken(mvc, HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deveRetornarPorId() throws Exception {
        mvc.perform(get("/api/usuarios/" + ID_USUARIO_HELPDESK)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ID_USUARIO_HELPDESK)))
                .andExpect(jsonPath("$.nome", is("HELPDESK")))
                .andExpect(jsonPath("$.nivelId", notNullValue()));
    }

    @Test
    public void deveRetornarPorCpf() throws Exception {
        mvc.perform(get("/api/usuarios?cpf=65710871036")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ID_USUARIO_HELPDESK)))
                .andExpect(jsonPath("$.nome", is("HELPDESK")));
    }

    @Test
    public void deveRetornarPorEmail() throws Exception {
        mvc.perform(get("/api/usuarios?email=HELPDESK@XBRAIN.COM.BR")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ID_USUARIO_HELPDESK)))
                .andExpect(jsonPath("$.nome", is("HELPDESK")));
    }

    @Test
    public void deveRetornarTodos() throws Exception {
        mvc.perform(get("/api/usuarios")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].nome", is("ADMIN")));
    }

    @Test
    public void deveFiltrarPorNome() throws Exception {
        mvc.perform(get("/api/usuarios?nome=ADMIN")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void deveValidarOsCamposNulosNoCadastro() throws Exception {
        mvc.perform(post("/api/usuarios")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new UsuarioDto())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(8)))
                .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                        "O campo nome é obrigatório.",
                        "O campo cpf é obrigatório.",
                        "O campo email é obrigatório.",
                        "O campo telefone é obrigatório.",
                        "O campo unidadeNegocioId é obrigatório.",
                        "O campo empresasId é obrigatório.",
                        "O campo cargoId é obrigatório.",
                        "O campo departamentoId é obrigatório.")));
    }

    @Test
    public void deveSalvar() throws Exception {
        UsuarioDto usuario = umUsuario("JOAO");
        mvc.perform(post("/api/usuarios")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
                .andExpect(status().isOk());

        List<Usuario> usuarios = Lists.newArrayList(
                repository.findAll(new UsuarioPredicate().comNome(usuario.getNome()).build()));

        assertEquals(usuarios.get(0).getNome(), usuario.getNome());
        assertEquals(usuarios.get(0).getCpf(), "09723864592");
    }

    @Test
    public void deveSalvarAsCidadesDoUsuario() throws Exception {
        UsuarioCidadeSaveDto dto = new UsuarioCidadeSaveDto();
        dto.setUsuarioId(ID_USUARIO_HELPDESK);
        dto.setCidadesId(Arrays.asList(736, 2921, 527));
        mvc.perform(post("/api/usuarios/cidades")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void deveSalvarAHierarquiaDoUsuario() throws Exception {
        UsuarioHierarquiaSaveDto dto = new UsuarioHierarquiaSaveDto();
        dto.setUsuarioId(ID_USUARIO_HELPDESK);
        dto.setHierarquiasId(Arrays.asList(100));
        mvc.perform(post("/api/usuarios/hierarquias")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void deveEditar() throws Exception {
        mvc.perform(post("/api/usuarios")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioParaEditar())))
                .andExpect(status().isOk());
        Usuario usuario = repository.findOne(ID_USUARIO_HELPDESK);
        Assert.assertEquals(usuario.getNome(), "JOAOZINHO");
    }

    @Test
    public void deveInativarUmUsuario() throws Exception {
        mvc.perform(post("/api/usuarios/inativar")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioParaInativar())))
                .andExpect(status().isOk());
        Usuario usuario = repository.findOne(ID_USUARIO_HELPDESK);
        Assert.assertEquals(usuario.getSituacao(), ESituacao.I);
    }

    @Test
    public void deveAtivarUmUsuario() throws Exception {
        mvc.perform(put("/api/usuarios/ativar")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioParaAtivar())))
                .andExpect(status().isOk());
        Usuario usuario = repository.findOne(ID_USUARIO_HELPDESK);
        Assert.assertEquals(usuario.getSituacao(), ESituacao.A);
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
        usuario.setNome("JOAOZINHO");
        return UsuarioDto.parse(usuario);
    }

    private UsuarioDto umUsuario(String nome) {
        UsuarioDto usuario = new UsuarioDto();
        usuario.setNome(nome);
        usuario.setCargoId(50);
        usuario.setDepartamentoId(50);
        usuario.setCpf("097.238.645-92");
        usuario.setUnidadeNegocioId(1);
        usuario.setEmpresasId(singletonList(4));
        usuario.setEmail("usuario@teste.com");
        usuario.setTelefone("43 995565661");
        return usuario;
    }
}
