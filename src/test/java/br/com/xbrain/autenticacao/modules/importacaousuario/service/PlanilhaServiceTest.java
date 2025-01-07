package br.com.xbrain.autenticacao.modules.importacaousuario.service;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import lombok.SneakyThrows;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;

import static br.com.xbrain.autenticacao.modules.comum.helper.FileHelper.umDocumentoPng;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@RunWith(MockitoJUnitRunner.class)
public class PlanilhaServiceTest {

    @InjectMocks
    private PlanilhaService service;

    @Test
    public void compararColunas_deveRetornarTrue_quandoValoresForemIguais() {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("Sheet 1");
        var row = sheet.createRow(0);
        var cell = row.createCell(0);
        cell.setCellValue("CamelCase e espaço no final     ");

        assertThat(PlanilhaService.compararColunas(cell, "camelcase e espaço no final"))
            .isTrue();

    }

    @Test
    public void compararColunas_deveRetornarFalse_quandoValoresNaoForemIguais() {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("Sheet 1");
        var row = sheet.createRow(0);
        var cell = row.createCell(0);
        cell.setCellValue("1");

        assertThat(PlanilhaService.compararColunas(cell, "2"))
            .isFalse();
    }

    @Test
    public void checkIfNotRowIsEmpty_deveRetornarTrue_quandoLinhaNaoForVazia() {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("Sheet 1");

        var row = sheet.createRow(1);
        row.createCell(0).setCellValue(12345678901L);

        assertThat(PlanilhaService.checkIfNotRowIsEmpty(row)).isTrue();
    }

    @Test
    public void checkIfNotRowIsEmpty_deveRetornarFalse_quandoLinhaForVazia() {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("Sheet 1");

        var row = sheet.createRow(0);

        assertThat(PlanilhaService.checkIfNotRowIsEmpty(row)).isFalse();
    }

    @SneakyThrows
    @Test
    public void getSheet_devesRetornarArquivo_quandoExtensaoArquivoForXlsx() {
        var arquivo = umMockMultipartFile();

        assertThat(service.getSheet(arquivo)).size().isEqualTo(49);
    }

    @Test
    public void getSheet_devesRetornarException_quandoExtensaoArquivoNaoForXlsx() {
        var arquivo = umDocumentoPng();

        assertThatCode(() -> service.getSheet(arquivo))
            .hasMessage("Não foi possível recuperar o sheet")
            .isInstanceOf(ValidacaoException.class);
    }

    private static MockMultipartFile umMockMultipartFile() throws Exception {
        var bytes = readAllBytes(get(requireNonNull(PlanilhaServiceTest.class.getClassLoader()
            .getResource("arquivo_usuario/planilha.xlsx")).getPath()));

        return new MockMultipartFile("file", "planilha.xlsx",
            "application/vnd.ms-excel", bytes);
    }
}
