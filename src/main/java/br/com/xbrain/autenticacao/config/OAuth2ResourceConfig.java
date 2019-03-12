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

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.*;

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
            "/api/usuarios/resetar-senha/**"
        };

        http
                .addFilterBefore(new CorsConfigFilter(), ChannelProcessingFilter.class)
                .requestMatchers()
                .antMatchers("/**")
                .and()
                .authorizeRequests()
                .antMatchers(permitAll).permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/api/usuarios/gerencia/**").hasAnyRole(
                        AUT_VISUALIZAR_USUARIO.name(), POL_GERENCIAR_USUARIOS_EXECUTIVO.name())
                .antMatchers("/api/emular**").hasRole(AUT_EMULAR_USUARIO.name())
                .antMatchers(HttpMethod.POST, "/api/cargos").hasRole(AUT_2023.name())
                .antMatchers(HttpMethod.PUT, "/api/cargos").hasRole(AUT_2023.name())
                .antMatchers("/api/funcionalidades").hasAnyRole(
                        AUT_VISUALIZAR_USUARIO.name(), POL_GERENCIAR_USUARIOS_EXECUTIVO.name())
                .antMatchers("/api/cargo-departamento-funcionalidade").hasRole(AUT_GER_PERMISSAO_CARGO_DEPARTAMENTO.name())
                .antMatchers("/api/permissoes-especiais").hasRole(AUT_GER_PERMISSAO_ESPECIAL_USUARIO.name())
                .antMatchers("/api/solicitacao-ramal").hasAnyRole(AUT_2033.name(), AUT_2034.name())
                .antMatchers("/api/solicitacao-ramal/gerencia/**").hasRole(AUT_2034.name())
                .anyRequest().authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenStore(new RedisTokenStore(redisConnectionFactory));
    }
}
