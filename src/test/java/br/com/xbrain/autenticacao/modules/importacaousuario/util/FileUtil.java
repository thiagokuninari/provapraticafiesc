package br.com.xbrain.autenticacao.modules.importacaousuario.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class FileUtil {

    public static byte[] getFile(String file) {
        final Logger log = LoggerFactory.getLogger(FileUtil.class);
        try {
            return Files.readAllBytes(
                    Paths.get(
                            Objects.requireNonNull(
                                    FileUtil.class
                                            .getClassLoader()
                                            .getResource(file))
                                    .getPath()));
        } catch (IOException ex) {
            log.error("Falha ao abrir arquivo.", ex);
            return null;
        }
    }
}
