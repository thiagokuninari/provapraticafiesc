package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static br.com.xbrain.autenticacao.modules.comum.enums.EErrors.ERRO_CONEXAO_MINIO;

@Configuration
public class MinioConfig {

    @Value("${app-config.minio.access.name}")
    String accessKey;
    @Value("${app-config.minio.access.secret}")
    String accessSecret;
    @Value("${app-config.minio.url}")
    String minioUrl;

    @Bean
    public MinioClient minioClient() {
        try {
            return new MinioClient(minioUrl, accessKey, accessSecret);
        } catch (InvalidEndpointException | InvalidPortException ex) {
            throw new IntegracaoException(ex, MinioConfig.class.getName(), ERRO_CONEXAO_MINIO);
        }
    }
}
