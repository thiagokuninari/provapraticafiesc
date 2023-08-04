package br.com.xbrain.autenticacao.modules.comum.helper;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.google.common.net.MediaType;
import lombok.SneakyThrows;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static com.google.common.io.ByteStreams.toByteArray;

public class FileHelper {

    @SneakyThrows
    public static MockMultipartFile umDocumentoPng() {
        var bytes = toByteArray(getFileInputStream("foto_usuario/file.png"));
        return new MockMultipartFile("foto_usuario",
            "file.png",
            "image/png",
            bytes);
    }

    public static InputStream getFileInputStream(String file) throws Exception {
        return new ByteArrayInputStream(
            Files.readAllBytes(Paths.get(
                Objects.requireNonNull(FileHelper.class.getClassLoader().getResource(file))
                    .getPath())));
    }

    public static Usuario umUsuario() {
        return Usuario.builder()
            .id(1)
            .fotoNomeOriginal("file.png")
            .fotoDiretorio("foto_usuario/")
            .fotoContentType(MediaType.JPEG.toString())
            .build();
    }
}
