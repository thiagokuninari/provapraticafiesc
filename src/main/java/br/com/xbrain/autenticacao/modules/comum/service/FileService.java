package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioFileService minioFileService;
    @Value("${app-config.upload-foto-usuario}")
    private String usuarioFotoDir;

    public void salvarArquivo(Usuario request, MultipartFile file) {
        var fileName = new SimpleDateFormat("yyyyMMdd_HHmmss_")
            .format(Calendar.getInstance().getTime()).concat(file.getOriginalFilename());

        request.setFotoContentType(file.getContentType());
        request.setFotoNomeOriginal(file.getOriginalFilename());
        request.setFotoDiretorio(usuarioFotoDir.concat("/").concat(fileName));

        try {
            minioFileService.salvarArquivo(file.getInputStream(), usuarioFotoDir.concat("/").concat(fileName));
        } catch (IOException ex) {
            throw new IntegracaoException(ex, MinioClient.class.getName(), EErrors.ERRO_SALVAR_ARQUIVO);
        }
    }
}
