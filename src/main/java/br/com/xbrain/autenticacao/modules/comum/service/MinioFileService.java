package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;

import static br.com.xbrain.autenticacao.modules.comum.enums.EErrors.*;

@Component
public class MinioFileService {

    @Value("${app-config.minio.bucket.name}")
    String defaultBucketName;

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
            if (checkArquivo(caminhoArquivo)) {
                return minioClient.getObject(defaultBucketName, caminhoArquivo);
            }
            return null;
        } catch (Exception ex) {
            throw new IntegracaoException(ex, MinioClient.class.getName(), ERRO_ACESSO_SERVIDOR);
        }
    }

    public boolean checkArquivo(String caminhoArquivo) {
        try {
            if (!ObjectUtils.isEmpty(minioClient.statObject(defaultBucketName, caminhoArquivo))) {
                return true;
            }
            throw new ValidacaoException(ARQUIVO_NAO_ENCONTRADO.getDescricao());
        } catch (Exception ex) {
            throw new IntegracaoException(ex, MinioClient.class.getName(), ERRO_ACESSO_SERVIDOR);
        }
    }
}
