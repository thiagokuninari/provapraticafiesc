package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

@Configuration
@EnableResourceServer
public class OAuth2ResourceConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private TokenStore tokenStore;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] permitAll = {
            "/call/**",
            "/parceiros-online/**",
            "/equipe-venda/**",
            "/api/usuarios/gerencia/acesso/senha",
            "/api/usuarios/gerencia/{idUsuario}/supervisor",
            "/api/cidades/{cidadeId}",
            "/api/usuarios/resetar-senha/**",
            "/api/public/disparar-timer-inativar-usuarios"
        };

        http
                .addFilterBefore(new CorsConfigFilter(), ChannelProcessingFilter.class)
                .requestMatchers()
                .antMatchers("/**")
                .and()
                .authorizeRequests()
                .antMatchers(permitAll).permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/api/usuarios/gerencia/**").hasRole(CodigoFuncionalidade.AUT_VISUALIZAR_USUARIO.name())
                .antMatchers("/api/emular**").hasRole(CodigoFuncionalidade.AUT_EMULAR_USUARIO.name())
                .antMatchers(HttpMethod.POST, "/api/cargos").hasRole(CodigoFuncionalidade.AUT_2023.name())
                .antMatchers(HttpMethod.PUT, "/api/cargos").hasRole(CodigoFuncionalidade.AUT_2023.name())
                .antMatchers("/api/funcionalidades").hasAnyRole(CodigoFuncionalidade.AUT_VISUALIZAR_USUARIO.name())
                .antMatchers("/api/cargo-departamento-funcionalidade")
                    .hasRole(CodigoFuncionalidade.AUT_GER_PERMISSAO_CARGO_DEPARTAMENTO.name())
                .antMatchers("/api/permissoes-especiais")
                    .hasRole(CodigoFuncionalidade.AUT_GER_PERMISSAO_ESPECIAL_USUARIO.name())
                .antMatchers("/api/solicitacao-ramal")
                    .hasAnyRole(CodigoFuncionalidade.CTR_2033.name(), CodigoFuncionalidade.CTR_2034.name())
                .antMatchers("/api/solicitacao-ramal/gerencia/**").hasRole(CodigoFuncionalidade.CTR_2034.name())
                .antMatchers("/api/usuarios/distribuicao/agendamentos/**").hasRole(CodigoFuncionalidade.MLG_5013.name())
                .anyRequest().authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenStore(tokenStore);
    }
}
