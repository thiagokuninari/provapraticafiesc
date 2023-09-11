package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.InputStream;

import static br.com.xbrain.autenticacao.modules.comum.helper.FileHelper.umDocumentoPng;
import static br.com.xbrain.autenticacao.modules.comum.helper.FileHelper.umUsuario;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FileServiceTest {

    @InjectMocks
    private FileService service;
    @Mock
    private MinioFileService minioFileService;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(service, "usuarioFotoDir", "desenvolvimento/autenticacao/usuario/foto");
        ReflectionTestUtils.setField(service, "urlEstatico", "src/test/resources/");
        ReflectionTestUtils.setField(service, "defaultBucketName", "conexao-claro-brasil");
        ReflectionTestUtils.setField(service, "minioUrl", "https://minio-dev.xbrain.com.br");
    }

    @Test
    public void salvarArquivo_deveFazerUpload_seSolicitado() throws NoSuchFieldException, IllegalAccessException {
        service.salvarArquivo(umUsuario(), umDocumentoPng());

        verify(minioFileService).salvarArquivo(any(InputStream.class), anyString());
    }

    @Test
    public void salvarArquivo_deveLancarEx_SeErroAoSalvar() {
        willAnswer(invocation -> {
            throw new IOException();
        }).given(minioFileService).salvarArquivo(any(InputStream.class), any());

        assertThatThrownBy(() -> service.salvarArquivo(umUsuario(), umDocumentoPng()))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#051 - Desculpe, ocorreu um erro interno. Contate o administrador.");
    }

    @Test
    public void buscaArquivosEstatico_deveRetornarListaDeArquivos_seSolicitado() throws IOException {
        var caminho = "foto_usuario/";

        var result = service.buscaArquivosEstatico(caminho);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
        assertEquals("download.jpeg", result.get().get(0).getName());
        assertEquals("file.png", result.get().get(1).getName());
    }
}
