package br.com.xbrain.autenticacao.modules.comum.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;

import static br.com.xbrain.autenticacao.modules.comum.helper.FileHelper.umDocumento;
import static br.com.xbrain.autenticacao.modules.comum.helper.FileHelper.umUsuario;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FileServiceTest {

    @InjectMocks
    private FileService service;
    @Mock
    private MinioFileService minioFileService;

    @Test
    public void salvarArquivo_deveFazerUpload_seSolicitado() throws NoSuchFieldException, IllegalAccessException {
        var usuarioFotoDir = "foto_usuario";
        var field = FileService.class.getDeclaredField("usuarioFotoDir");
        field.setAccessible(true);
        field.set(service, usuarioFotoDir);
        service.salvarArquivo(umUsuario(), umDocumento());

        verify(minioFileService).salvarArquivo(any(InputStream.class), anyString());
    }
}
