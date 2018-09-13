package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioImportacaoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioImportacaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.util.FileUtil.getFile;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class ImportacaoUsuarioServiceTest {

    @Autowired
    ImportacaoUsuarioService importacaoUsuarioService;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    UsuarioRepository usuarioRepository;

    MockMultipartFile mockMultipartFile;

    @Before
    public void setUp() throws Exception {
        mockMultipartFile = new MockMultipartFile("file", "planilha.xlsx",
                "application/vnd.ms-excel", getFile("arquivo_usuario/planilha.xlsx"));
    }

    @Test
    public void readFile() {
        List<UsuarioImportacaoRequest> usuarioUploadFiles = importacaoUsuarioService
                .readFile(mockMultipartFile, true);
        assertEquals(usuarioUploadFiles.size(), 43);
    }

    @Test
    public void removerUsuariosJaSalvos() {
        List<Usuario> usuarios = buildUsuarios();
        assertEquals(usuarios.size(), 4);
        usuarios = importacaoUsuarioService.removerUsuariosJaSalvos(usuarios);
        assertEquals(usuarios.size(), 2);
    }

    @Test
    public void validarUsuarioExistente() {
        Usuario usuario = new Usuario();

        usuario.setEmail("ADMIN@XBRAIN.COM.BR");
        importacaoUsuarioService.validarUsuarioExistente(usuario);
        assertFalse(importacaoUsuarioService.validarUsuarioExistente(usuario));

        usuario.setEmail("ADMINTESTE@XBRAIN.COM.BR");
        importacaoUsuarioService.validarUsuarioExistente(usuario);
        assertTrue(importacaoUsuarioService.validarUsuarioExistente(usuario));

        usuario.setCpf("38957979875");
        importacaoUsuarioService.validarUsuarioExistente(usuario);
        assertFalse(importacaoUsuarioService.validarUsuarioExistente(usuario));

        usuario.setCpf("9612473633");
        importacaoUsuarioService.validarUsuarioExistente(usuario);
        assertTrue(importacaoUsuarioService.validarUsuarioExistente(usuario));

    }

    @Test
    public void salvarUsuarioFile() {
        UsuarioImportacaoRequest usuarioImportacaoRequest = new UsuarioImportacaoRequest();
        usuarioImportacaoRequest.setSenhaPadrao(true);
        List<UsuarioImportacaoResponse> usuarioUploadFiles = importacaoUsuarioService
                .salvarUsuarioFile(mockMultipartFile, usuarioImportacaoRequest);
        assertEquals(usuarioUploadFiles.size(), 43);
    }

    private List<Usuario> buildUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        Usuario usuario = new Usuario();
        usuario.setEmail("ADMIN@XBRAIN.COM.BR");
        usuarios.add(usuario);
        usuario = new Usuario();
        usuario.setEmail("ADMINTESTE@XBRAIN.COM.BR");
        usuarios.add(usuario);
        usuario = new Usuario();
        usuario.setCpf("38957979875");
        usuarios.add(usuario);
        usuario = new Usuario();
        usuario.setCpf("9612473633");
        usuarios.add(usuario);

        return usuarios;
    }
}