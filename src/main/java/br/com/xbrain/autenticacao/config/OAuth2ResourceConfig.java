package br.com.xbrain.autenticacao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

@Configuration
@EnableResourceServer
public class OAuth2ResourceConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    public CustomJdbcTokenStore customJdbcTokenStore;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] permitAll = {"/call/**", "/parceiros-online/**", "/api/usuarios/gerencia/{idUsuario}/supervisor"};

        http
                .addFilterBefore(new CorsConfigFilter(), ChannelProcessingFilter.class)
                .requestMatchers().antMatchers("/**")
                .and()
                .authorizeRequests()
                .antMatchers(permitAll).permitAll()
                .antMatchers("/api/usuarios/gerencia/**").hasAnyRole("AUT_VISUALIZAR_USUARIO", "POL_GERENCIAR_USUARIOS_EXECUTIVO")
                .antMatchers("/api/emular**").hasRole("AUT_EMULAR_USUARIO")
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources)
            throws Exception {
        resources.tokenStore(customJdbcTokenStore);
    }
}
