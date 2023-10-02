package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.thymeleaf.util.StringUtils.concat;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioFileService minioFileService;
    @Value("${app-config.upload-foto-usuario}")
    private String usuarioFotoDir;
    @Value("${app-config.url-foto-usuario}")
    private String urlFotoUsuario;

    public void salvarArquivo(Usuario request, MultipartFile file) {
        var fileName = new SimpleDateFormat("yyyyMMdd_HHmmss_")
            .format(Calendar.getInstance().getTime()).concat(file.getOriginalFilename());

        request.setFotoContentType(file.getContentType());
        request.setFotoNomeOriginal(file.getOriginalFilename());
        request.setFotoDiretorio(concat(urlFotoUsuario, fileName));

        try {
            minioFileService.salvarArquivo(file.getInputStream(), concat(usuarioFotoDir, fileName));
        } catch (IOException ex) {
            throw new IntegracaoException(ex, MinioClient.class.getName(), EErrors.ERRO_SALVAR_ARQUIVO);
        }
    }
}
