package br.com.xbrain.autenticacao.config.feign;

import feign.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


public class FeignTimeoutConfiguration {

    @Value("${app-config.feign.connect-timeout}")
    private Integer connectTimeout;
    @Value("${app-config.feign.read-timeout}")
    private Integer readTimeout;

    @Bean
    public Request.Options options() {
        return new Request.Options(connectTimeout, readTimeout);
    }
}
