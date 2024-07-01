package br.com.xbrain.autenticacao.modules.comum.util;

import lombok.SneakyThrows;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class FileUtilsTest {

    @Test
    @SneakyThrows
    public void saveFile_deveRetornarTrue_quandoArquivoSalvoComSucesso() {
        var multipartFile = Mockito.mock(MultipartFile.class);
        doNothing().when(multipartFile).transferTo(new File("diretorio"));

        assertThat(FileUtils.saveFile(multipartFile, "diretorio"))
            .isTrue();

        verify(multipartFile, Mockito.times(1)).transferTo(new File("diretorio"));
    }

    @Test
    @SneakyThrows
    public void saveFile_deveRetornarFalse_quandoArquivoNaoSalvo() {
        var multipartFile = Mockito.mock(MultipartFile.class);
        doThrow(new IOException("File transfer failed")).when(multipartFile).transferTo(new File("diretorio"));

        assertThat(FileUtils.saveFile(multipartFile, "diretorio"))
            .isFalse();
        verify(multipartFile, Mockito.times(1)).transferTo(new File("diretorio"));
    }
}
