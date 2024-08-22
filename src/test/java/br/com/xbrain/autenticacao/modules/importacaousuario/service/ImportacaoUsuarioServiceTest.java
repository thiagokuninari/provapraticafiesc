package br.com.xbrain.autenticacao.modules.importacaousuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoRequest;
import br.com.xbrain.autenticacao.modules.importacaousuario.dto.UsuarioImportacaoResponse;
import br.com.xbrain.autenticacao.modules.importacaousuario.util.NumeroCelulaUtil;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.api.Assertions;
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
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


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

    @Test
    public void readFile_deveLancarException_quandoErroNaPlanilhaService() {
        doThrow(ValidacaoException.class)
            .when(planilhaService)
            .getSheet(any());

        var request = new UsuarioImportacaoRequest();

        Assertions.assertThatCode(() -> importacaoUsuarioService.readFile(mockMultipartFile, request))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Erro. Arquivo Inválido.");

        verify(planilhaService).getSheet(any());
    }

    @Test
    @SneakyThrows
    public void readFile_deveLancarValidacaoException_quandoNumeroDeColunasInsuficiente() {
        var arquivo = umMockMultipartFile();
        var request = new UsuarioImportacaoRequest();
        request.setSenhaPadrao(true);

        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("Sheet1");
        var row = sheet.createRow(NumeroCelulaUtil.PRIMEIRA_LINHA);
        row.createCell(0).setCellValue("Coluna 1");

        when(planilhaService.getSheet(arquivo)).thenReturn(sheet);

        Assertions.assertThatCode(() -> importacaoUsuarioService.readFile(arquivo, request))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Erro. Arquivo Inválido.");

        verify(planilhaService).getSheet(arquivo);
    }

    private static MockMultipartFile umMockMultipartFile() throws Exception {
        var bytes = readAllBytes(get(requireNonNull(PlanilhaServiceTest.class.getClassLoader()
            .getResource("arquivo_usuario/planilha.xlsx")).getPath()));

        return new MockMultipartFile("file", "planilha.xlsx",
            "application/vnd.ms-excel", bytes);
    }
}
