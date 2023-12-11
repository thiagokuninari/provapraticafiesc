package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.InputStream;

import static br.com.xbrain.autenticacao.modules.comum.helper.FileHelper.getFileInputStream;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MinioFileServiceTest {

    private static final String TEST_FILE_DIRETORIO = "foto_usuario/file.png";
    @InjectMocks
    private MinioFileService service;
    @Mock
    private MinioClient client;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(service, "defaultBucketName", "conexao-claro-brasil");
    }

    @Test
    @SneakyThrows
    public void salvarArquivo_deveSalvarArquivoMultipart_quandoSolicitado() {
        var file = getFileInputStream(TEST_FILE_DIRETORIO);

        assertThatCode(() -> service.salvarArquivo(file, "arquivo-teste"))
            .doesNotThrowAnyException();
    }

    @Test
    @SneakyThrows
    public void salvarArquivo_deveLancarIntegracaoException_quandoOcorrerErroAoSalvarMultipart() {
        var file = getFileInputStream(TEST_FILE_DIRETORIO);

        doThrow(IntegracaoException.class)
            .when(client).putObject(any(), any(), any(InputStream.class), any());

        assertThatThrownBy(() -> service.salvarArquivo(file, "arquivo-teste"))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#051 - Desculpe, ocorreu um erro interno. Contate o administrador.");
    }

    @Test
    @SneakyThrows
    public void salvarArquivo_deveSalvarArquivoInputStream_quandoSolicitado() {
        var file = getFileInputStream(TEST_FILE_DIRETORIO);

        assertThatCode(() -> service.salvarArquivo(file, "arquivo-teste"))
            .doesNotThrowAnyException();
    }

    @Test
    @SneakyThrows
    public void salvarArquivo_deveLancarIntegracaoException_quandoOcorrerErroAoSalvarInputStream() {
        var file = getFileInputStream(TEST_FILE_DIRETORIO);

        doThrow(RuntimeException.class)
            .when(client).putObject(any(), any(), any(InputStream.class), any());

        assertThatThrownBy(() -> service.salvarArquivo(file, "arquivo-teste"))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#051 - Desculpe, ocorreu um erro interno. Contate o administrador.");
    }

    @Test
    @SneakyThrows
    public void getArquivo_deveRetornarArquivo_quandoArquivoExistirNoMinIO() {
        var file = getFileInputStream(TEST_FILE_DIRETORIO);

        when(client.getObject(any(), eq(TEST_FILE_DIRETORIO))).thenReturn(file);

        assertThat(service.getArquivo(TEST_FILE_DIRETORIO)).isEqualTo(file);
    }

    @Test
    @SneakyThrows
    public void getArquivo_deveLancarNotFoundException_quandoArquivoNaoExistirNoMinIO() {
        doThrow(ErrorResponseException.class)
            .when(client).getObject(any(), eq(TEST_FILE_DIRETORIO));

        assertThatThrownBy(() -> service.getArquivo(TEST_FILE_DIRETORIO))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Arquivo não encontrado.");
    }
}
