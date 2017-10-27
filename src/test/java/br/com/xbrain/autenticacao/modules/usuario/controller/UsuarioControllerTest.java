package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import com.google.common.collect.Lists;
import helpers.Usuarios;
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

import javax.persistence.EntityManager;
import java.util.List;

import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/usuarios.sql"})
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void deveSolicitarAutenticacao() throws Exception {
        mvc.perform(get("/api/usuarios")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deveTerPermissaoDeGerenciaDeUsuario() throws Exception {
        mvc.perform(get("/api/usuarios")
                .header("Authorization", getAccessToken(mvc, Usuarios.HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deveRetornarPorId() throws Exception {
        mvc.perform(get("/api/usuarios/100")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.nome", is("ADMIN")));
    }

    @Test
    public void deveRetornarTodos() throws Exception {
        mvc.perform(get("/api/usuarios")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].nome", is("ADMIN")));
    }

    @Test
    public void deveFiltrarPorNome() throws Exception {
        mvc.perform(get("/api/usuarios?nome=ADMIN")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void deveSalvar() throws Exception {
        Usuario usuario = umUsuario("JOAO");
        mvc.perform(post("/api/usuarios")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
                .andExpect(status().isOk());

        List<Usuario> usuarios = Lists.newArrayList(
                repository.findAll(new UsuarioPredicate().comNome(usuario.getNome()).build()));

        assertEquals(usuarios.get(0).getNome(), usuario.getNome());
    }

    @Test
    public void deveValidarOsCamposNulosNoCadastro() throws Exception {
        mvc.perform(post("/api/usuarios")
                .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new Usuario())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                        "O campo nome é obrigatório.",
                        "O campo cpf é obrigatório.",
                        "O campo email é obrigatório.",
                        "O campo telefone é obrigatório.",
                        "O campo cargo é obrigatório.",
                        "O campo departamento é obrigatório.")));
    }

    private Usuario umUsuario(String nome) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setCargo(new Cargo(1));
        usuario.setDepartamento(new Departamento(1));
        usuario.setCpf("097.238.645-92");
        usuario.setEmail("usuario@teste.com");
        usuario.setTelefone("43 995565661");
        return usuario;
    }
}
