package br.com.xbrain.autenticacao.modules.comum.util;

import lombok.SneakyThrows;
import org.junit.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.*;

public class CsvUtilsTest {

    @Test
    public void createBomStream_deveRetornarByteArrayOutputStreamComParametrosPredefinidos() {
        var PARAMETROS_UM = 239;
        var PARAMETROS_DOIS = 187;
        var PARAMETROS_TRES = 191;
        ByteArrayOutputStream byteArrayOutputStream = CsvUtils.createBomStream();

        byte[] bytesEsperados = {(byte) PARAMETROS_UM, (byte) PARAMETROS_DOIS, (byte) PARAMETROS_TRES};
        byte[] bytesReais = byteArrayOutputStream.toByteArray();

        assertArrayEquals(bytesEsperados, bytesReais);
    }

    @Test
    @SneakyThrows
    public void setCsvNoHttpResponse_deveRetornarTrue_quandoTodosOsParametrosForemValidos() {
        var csvContent = "coluna1,coluna2\nvalor1,valor2";
        var fileName = "teste.csv";

        var response = mock(HttpServletResponse.class);
        var outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);

        assertThat(CsvUtils.setCsvNoHttpResponse(csvContent, fileName, response)).isTrue();
        verify(response).setContentType("text/csv; charset=UTF-8");
        verify(response).setHeader("Content-Disposition", "attachment; filename=" + fileName);
        verify(response).getOutputStream();
        verify(outputStream, atLeastOnce()).write(any(byte[].class), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    public void setCsvNoHttpResponse_deveRetornarFalse_quandoOcorrerIOException() {
        var csvContent = "coluna1,coluna2\nvalor1,valor2";
        var fileName = "teste.csv";

        var response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenThrow(new IOException());

        assertThat(CsvUtils.setCsvNoHttpResponse(csvContent, fileName, response)).isFalse();
        verify(response).setContentType("text/csv; charset=UTF-8");
        verify(response).setHeader("Content-Disposition", "attachment; filename=" + fileName);
        verify(response).getOutputStream();
    }

    @Test
    public void replaceCaracteres_deveSubstituirOPontoEVirgulaPorVazio_quandoFornecidoAString() {
        assertThat(CsvUtils.replaceCaracteres("pon;to; e ;vir;gula"))
            .isEqualTo("ponto e virgula");
    }

    @Test
    public void replaceCaracteres_deveSubstituirAVirgulaPorPonto_quandoFornecidoAString() {
        assertThat(CsvUtils.replaceCaracteres("ponto, ponto, ponto,"))
            .isEqualTo("ponto. ponto. ponto.");
    }

    @Test
    public void replaceCaracteres_deveSubstituirOBarraNPorEspaco_quandoFornecidoAString() {
        assertThat(CsvUtils.replaceCaracteres("espaco\nespaco\nespaco"))
            .isEqualTo("espaco espaco espaco");
    }

    @Test
    public void replaceCaracteres_deveSubstituirOBarraRPorVazio_quandoFornecidoAString() {
        assertThat(CsvUtils.replaceCaracteres("retorno\r\rRetorno"))
            .isEqualTo("retornoRetorno");
    }

    @Test
    public void replaceCaracteres_deveSubstituirOBarraTPorVazio_quandoFornecidoAString() {
        assertThat(CsvUtils.replaceCaracteres("tab\t\tTab"))
            .isEqualTo("tabTab");
    }

    @Test
    public void createFileName_deveCriarNomeConcatenandoADataComIdEPontoCsv_quandoSolicitado() {
        var result = CsvUtils.createFileName("nomeRelatorio", 123456789);
        assertThat(result).isNotNull();
        assertThat(result).contains("_123456789.csv");
    }
}
