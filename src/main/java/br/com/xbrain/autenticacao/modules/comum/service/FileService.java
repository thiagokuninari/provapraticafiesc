package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioFileService minioFileService;
    @Value("${app-config.upload-foto-usuario}")
    private String usuarioFotoDir;
    @Value("${app-config.url-estatico}")
    private String urlEstatico;

    public void salvarArquivo(Usuario request, MultipartFile file) {
        var fileName = new SimpleDateFormat("yyyyMMdd_HHmmss_")
            .format(Calendar.getInstance().getTime()).concat(file.getOriginalFilename());

        request.setFotoContentType(file.getContentType());
        request.setFotoNomeOriginal(file.getOriginalFilename());
        request.setFotoDiretorio(usuarioFotoDir.concat(fileName));

        try {
            minioFileService.salvarArquivo(file.getInputStream(), usuarioFotoDir.concat("/").concat(fileName));
        } catch (IOException ex) {
            throw new IntegracaoException(ex, MinioClient.class.getName(), EErrors.ERRO_SALVAR_ARQUIVO);
        }
    }

    public Optional<List<File>> buscaArquivosEstatico(String caminho) throws IOException {
        var path = Paths.get(urlEstatico.concat(caminho));
        try (var stream = Files.walk(path, Integer.MAX_VALUE)) {
            return Optional.of(stream
                .map(String::valueOf)
                .map(File::new)
                .filter(File::isFile)
                .sorted()
                .collect(Collectors.toList()));
        }
    }
}
