package br.com.xbrain.autenticacao.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("producao")
public class ReCaptchaFilterConfig {

    @Bean
    public FilterRegistrationBean filter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new ReCaptchaFilter());
        registration.addUrlPatterns("/oauth/token/*");
        return registration;
    }
}
