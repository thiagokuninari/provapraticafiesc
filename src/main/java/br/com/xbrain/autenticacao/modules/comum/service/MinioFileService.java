package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.ErrorResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

import static br.com.xbrain.autenticacao.modules.comum.enums.EErrors.ERRO_ACESSO_SERVIDOR;
import static br.com.xbrain.autenticacao.modules.comum.enums.EErrors.ERRO_SALVAR_ARQUIVO;

@Component
public class MinioFileService {

    @Value("${app-config.minio.bucket.name}")
    private String defaultBucketName;

    @Autowired
    private MinioClient minioClient;

    public void salvarArquivo(InputStream arquivo, String nomeArquivo) {
        try {
            var options = new PutObjectOptions(-1, PutObjectOptions.MAX_PART_SIZE);
            minioClient.putObject(defaultBucketName, nomeArquivo, arquivo, options);
        } catch (Exception ex) {
            throw new IntegracaoException(ex, MinioClient.class.getName(), ERRO_SALVAR_ARQUIVO);
        }
    }

    public InputStream getArquivo(String caminhoArquivo) {
        try {
            return minioClient.getObject(defaultBucketName, caminhoArquivo);
        } catch (ErrorResponseException ex) {
            throw new NotFoundException("Arquivo não encontrado.");
        } catch (Exception ex) {
            throw new IntegracaoException(ex, MinioClient.class.getName(), ERRO_ACESSO_SERVIDOR);
        }
    }
}
