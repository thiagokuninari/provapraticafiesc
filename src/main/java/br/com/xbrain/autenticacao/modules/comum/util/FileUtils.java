package br.com.xbrain.autenticacao.modules.comum.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class FileUtils {

    public static boolean saveFile(MultipartFile file, String diretorio) {
        try {
            file.transferTo(new File(diretorio));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
