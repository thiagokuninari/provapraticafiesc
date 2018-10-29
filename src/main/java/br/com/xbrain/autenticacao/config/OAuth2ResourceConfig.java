package br.com.xbrain.autenticacao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

@Configuration
@EnableResourceServer
public class OAuth2ResourceConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] permitAll = {
            "/call/**",
            "/parceiros-online/**",
            "/equipe-venda/**",
            "/api/usuarios/gerencia/acesso/senha",
            "/api/usuarios/gerencia/{idUsuario}/supervisor",
            "/api/cidades/{cidadeId}",
            "/api/public/disparar-timer-inativar-usuarios",
            "/api/usuarios/resetar-senha/**"
        };

        http
                .addFilterBefore(new CorsConfigFilter(), ChannelProcessingFilter.class)
                .requestMatchers().antMatchers("/**")
                .and()
                .authorizeRequests()
                .antMatchers(permitAll).permitAll()
                .antMatchers("/api/usuarios/gerencia/**").hasAnyRole("AUT_VISUALIZAR_USUARIO", "POL_GERENCIAR_USUARIOS_EXECUTIVO")
                .antMatchers("/api/emular**").hasRole("AUT_EMULAR_USUARIO")
                .antMatchers(HttpMethod.POST, "/api/cargos").hasRole("AUT_2023")
                .antMatchers(HttpMethod.PUT, "/api/cargos").hasRole("AUT_2023")
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenStore(new RedisTokenStore(redisConnectionFactory));
    }
}
