package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

import java.util.Arrays;

import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.TEST;

@Configuration
@EnableResourceServer
public class OAuth2ResourceConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private CorsConfigFilter corsConfigFilter;
    @Autowired
    private Environment environment;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] permitAll = {
            "/call/**",
            "/api/public/**",
            "/parceiros-online/**",
            "/equipe-venda/**",
            "/api/ufs",
            "/api/cidades",
            "/api/usuarios/gerencia/acesso/senha",
            "/api/usuarios/gerencia/{idUsuario}/supervisor",
            "/api/cidades/{cidadeId}",
            "/api/usuarios/resetar-senha/**",
            "/api/public/disparar-timer-inativar-usuarios",
            "/api/usuarios/usuario-funil-prospeccao",
            "/api/usuarios/gerencia/existir/usuario",
            "/api/cep/**",
            "/api/usuarios/usuario-funil-prospeccao",
            "/api/sites/{id}/supervisores",
            "/api/sites/permitidos"
        };

        http
            .addFilterBefore(corsConfigFilter, ChannelProcessingFilter.class)
            .requestMatchers()
            .antMatchers("/**")
            .and()
            .authorizeRequests()
            .antMatchers(permitAll).permitAll()
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/usuarios/vendedores-feeder").hasAnyRole(
                CodigoFuncionalidade.APPLICATION.name(),
                CodigoFuncionalidade.FDR_GERENCIAR_LEAD.name(),
                CodigoFuncionalidade.MLG_5018.name())
            .antMatchers("/api/usuarios/situacoes/timer").hasAnyRole(
                CodigoFuncionalidade.APPLICATION.name(),
                CodigoFuncionalidade.AUT_VISUALIZAR_USUARIO.name())
            .regexMatchers("/api/sites/(\\d+)").authenticated()
            .antMatchers("/api/usuarios/responsaveis-ddd").authenticated()
            .antMatchers("/api/usuarios/gerencia/chamados/usuarios-redirecionamento/*").authenticated()
            .antMatchers("/api/usuarios/gerencia/**").hasRole(
                CodigoFuncionalidade.AUT_VISUALIZAR_USUARIO.name())
            .antMatchers("/api/usuarios/bko-centralizado/**").hasRole(
                CodigoFuncionalidade.BKO_PRIORIZAR_INDICACOES.name())
            .antMatchers("/api/emular**").hasRole(
                CodigoFuncionalidade.AUT_EMULAR_USUARIO.name())
            .antMatchers(HttpMethod.POST, "/api/cargos").hasRole(
                CodigoFuncionalidade.AUT_2023.name())
            .antMatchers(HttpMethod.PUT, "/api/cargos").hasRole(
                CodigoFuncionalidade.AUT_2023.name())
            .antMatchers("/api/funcionalidades").hasAnyRole(
                CodigoFuncionalidade.AUT_VISUALIZAR_USUARIO.name())
            .antMatchers("/api/feriado/gerenciar/**").hasRole(
                CodigoFuncionalidade.CTR_2050.name())
            .antMatchers("/api/cargo-departamento-funcionalidade").hasRole(
                CodigoFuncionalidade.AUT_GER_PERMISSAO_CARGO_DEPARTAMENTO.name())
            .antMatchers("/api/permissoes-especiais").hasRole(
                CodigoFuncionalidade.AUT_GER_PERMISSAO_ESPECIAL_USUARIO.name())
            .antMatchers("/api/solicitacao-ramal").hasAnyRole(
                CodigoFuncionalidade.CTR_2033.name(),
                CodigoFuncionalidade.CTR_2034.name(),
                CodigoFuncionalidade.CTR_20014.name(),
                CodigoFuncionalidade.CTR_20015.name())
            .antMatchers("/api/solicitacao-ramal/gerencia/**").hasRole(
                CodigoFuncionalidade.CTR_2034.name())
            .antMatchers("/api/usuarios/distribuicao/agendamentos/**").hasRole(
                CodigoFuncionalidade.MLG_5013.name())
            .antMatchers("/api/logout/todos-usuarios").hasRole(
                CodigoFuncionalidade.AUT_DESLOGAR_USUARIO.name())
            .antMatchers("/api/relatorio-login-logout/entre-datas").authenticated()
            .antMatchers("/api/relatorio-login-logout/**").hasRole(
                CodigoFuncionalidade.AUT_2100.name())
            .antMatchers(HttpMethod.GET, "/api/sites/**").hasAnyRole(
                CodigoFuncionalidade.AUT_2046.name(),
                CodigoFuncionalidade.APPLICATION.name())
            .antMatchers("/api/sites/**").hasAnyRole(
                CodigoFuncionalidade.AUT_2047.name(),
                CodigoFuncionalidade.APPLICATION.name())
            .antMatchers("/api/usuario/site**").hasAnyRole(
                CodigoFuncionalidade.AUT_2046.name())
            .antMatchers("/api/usuario-acesso/inativar").hasRole(
                CodigoFuncionalidade.AUT_INATIVAR_USUARIOS_SEM_ACESSO.name())
            .antMatchers("/api/horarios-acesso", "/api/horarios-acesso/{id}/**").hasAnyRole(
                CodigoFuncionalidade.AUT_20009.name(),
                CodigoFuncionalidade.AUT_20024.name())
            .antMatchers("/api/horarios-acesso/status", "/api/horarios-acesso/status/{siteId}").hasAnyRole(
                CodigoFuncionalidade.AUT_20024.name())
            .antMatchers("/api/organizacao-empresa/**",
                "/api/organizacao-empresa-historico/**",
                "/api/nivel-empresa/**",
                "/api/modalidade-empresa/**")
            .hasRole(CodigoFuncionalidade.VAR_GERENCIAR_ORGANIZACOES_VAREJO_RECEPTIVO.name())
            .anyRequest().authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        if (isActiveProfileTest()) {
            resources.stateless(false);
        }
        resources.tokenStore(tokenStore);
    }

    private boolean isActiveProfileTest() {
        return Arrays.stream(environment.getActiveProfiles()).anyMatch(env -> env.equalsIgnoreCase(TEST));
    }
}
