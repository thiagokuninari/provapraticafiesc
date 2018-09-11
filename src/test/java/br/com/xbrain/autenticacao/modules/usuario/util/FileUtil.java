package br.com.xbrain.autenticacao.modules.usuario.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
    public static byte[] getFile(String file) {
        try {
            return Files.readAllBytes(
                    Paths.get(FileUtil.class.getClassLoader().getResource(file).getPath()));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
