package br.com.xbrain.autenticacao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.validation.Validation;
import javax.validation.Validator;

@Configuration
public class BeansConfig {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public Validator getValidator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }
}
