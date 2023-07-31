package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.util.FileUtils;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Slf4j
@Service
public class FileService {

    @Value("${app-config.usuario-foto.dir}")
    private String usuarioFotoDir;
    @Value("${app-config.usuario-foto.url}")
    private String usuarioFotoUrl;

    public void uploadFotoUsuario(Usuario request, MultipartFile file) {
        try {
            if (!Files.exists(Paths.get(usuarioFotoDir))) {
                Files.createDirectories(Paths.get(usuarioFotoDir));
            }

            var fileName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(Calendar.getInstance().getTime())
                    .concat(file.getOriginalFilename());

            request.setFotoContentType(file.getContentType());
            request.setFotoNomeOriginal(file.getOriginalFilename());
            request.setFotoDiretorio(usuarioFotoUrl.concat("/").concat(fileName));
            saveFile(file, usuarioFotoDir.concat("/").concat(fileName));
        } catch (Exception ex) {
            log.error("Erro ao gravar foto do usuário: " + usuarioFotoDir, ex);
        }
    }

    @Async
    public void saveFile(MultipartFile file, String diretorioFile) {
        FileUtils.saveFile(file, diretorioFile);
    }
}
