package br.com.xbrain.autenticacao.modules.importacaousuario.service;

import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoRequest;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDadosAcessoRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

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

    MockMultipartFile mockMultipartFile;

    @Before
    public void setUp() throws Exception {
        mockMultipartFile = new MockMultipartFile("file", "planilha.xlsx",
                "application/vnd.ms-excel", getFile("arquivo_usuario/planilha.xlsx"));
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