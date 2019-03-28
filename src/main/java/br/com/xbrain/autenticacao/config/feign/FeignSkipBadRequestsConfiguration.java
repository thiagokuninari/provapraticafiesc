package br.com.xbrain.autenticacao.config.feign;

import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.util.Constantes;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

@Configuration
public class FeignSkipBadRequestsConfiguration {

    @Bean
    public ErrorDecoder errorDecoder() throws IOException {
        return (methodKey, response) -> {
            int status = response.status();
            if (status == Constantes.BAD_REQUEST || status == Constantes.NOT_FOUND) {
                return new FeignBadResponseWrapper(status, getHttpHeaders(response), getBody(response));
            } else {
                return new RuntimeException("Response Code " + status);
            }
        };
    }

    private String getBody(Response response) {
        try {
            return IOUtils.toString(response.body().asReader());
        } catch (Exception ignored) {
            throw new IntegracaoException(ignored,
                    FeignSkipBadRequestsConfiguration.class.getName(),
                    EErrors.ERRO_CONVERTER_EXCEPTION);
        }
    }

    private HttpHeaders getHttpHeaders(Response response) {
        HttpHeaders httpHeaders = new HttpHeaders();
        response.headers().forEach((content, value) -> httpHeaders.add("feign-" + content, StringUtils.join(value,",")));
        return httpHeaders;
    }
}
