package br.com.xbrain.autenticacao.modules.importacaousuario.service;

import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoPlanilha;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoRequest;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoResponse;
import br.com.xbrain.autenticacao.modules.importacaousuario.service.ImportacaoUsuarioService;
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

import java.util.List;

import static br.com.xbrain.autenticacao.modules.importacaousuario.util.FileUtil.getFile;
import static org.junit.Assert.assertEquals;


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
        UsuarioImportacaoRequest usuarioImportacaoRequest = new UsuarioImportacaoRequest(true);
        List<UsuarioImportacaoPlanilha> usuarioUploadFiles = importacaoUsuarioService
                .readFile(mockMultipartFile, usuarioImportacaoRequest);
        assertEquals(usuarioUploadFiles.size(), 15);
    }

    @Test
    public void deveRetornarOResultadoDaImportacaoQuandoErroOuSucesso() {
        UsuarioImportacaoRequest usuarioImportacaoRequest = new UsuarioImportacaoRequest();
        usuarioImportacaoRequest.setSenhaPadrao(true);
        List<UsuarioImportacaoResponse> usuarioUploadFiles = importacaoUsuarioService
                .salvarUsuarioFile(mockMultipartFile, usuarioImportacaoRequest);
        assertEquals(usuarioUploadFiles.size(), 15);
    }

}