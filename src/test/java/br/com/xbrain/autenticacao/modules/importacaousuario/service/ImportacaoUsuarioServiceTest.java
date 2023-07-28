package br.com.xbrain.autenticacao.modules.importacaousuario.service;

import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoRequest;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoResponse;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoPlanilhaTest.umUsuarioImportacao;
import static br.com.xbrain.autenticacao.modules.importacaousuario.util.FileUtil.getFile;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ImportacaoUsuarioServiceTest {

    @InjectMocks
    private ImportacaoUsuarioService importacaoUsuarioService;
    @Mock
    private PlanilhaService planilhaService;
    @Mock
    private UsuarioUploadFileService usuarioUploadFileService;

    MockMultipartFile mockMultipartFile;
    Sheet sheet;

    @Before
    public void setUp() throws Exception {
        mockMultipartFile = new MockMultipartFile("file", "planilha.xlsx",
                "application/vnd.ms-excel", getFile("arquivo_usuario/planilha.xlsx"));
        sheet = new XSSFWorkbook(mockMultipartFile.getInputStream()).getSheetAt(0);
    }

    @Test
    public void salvarUsuarioFile_deveRetornarOResultadoDaImportacao_quandoErroOuSucesso() {
        when(planilhaService.getSheet(mockMultipartFile)).thenReturn(sheet);
        when(usuarioUploadFileService.processarUsuarios(any(), any())).thenReturn(umUsuarioImportacao("11999933312"));

        UsuarioImportacaoRequest usuarioImportacaoRequest = new UsuarioImportacaoRequest();
        usuarioImportacaoRequest.setSenhaPadrao(true);
        List<UsuarioImportacaoResponse> usuarioUploadFiles = importacaoUsuarioService
                .salvarUsuarioFile(mockMultipartFile, usuarioImportacaoRequest);

        assertEquals(usuarioUploadFiles.size(), 15);
    }
}
