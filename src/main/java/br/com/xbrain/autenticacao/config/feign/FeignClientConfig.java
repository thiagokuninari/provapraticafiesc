package br.com.xbrain.autenticacao.config.feign;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestTokenBearerInterceptor() {
        return requestTemplate -> requestTemplate.header("Authorization", "Bearer " + getToken());
    }

    private String getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((OAuth2AuthenticationDetails) authentication.getDetails()).getTokenValue();
    }
}
