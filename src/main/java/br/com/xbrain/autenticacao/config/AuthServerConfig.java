package br.com.xbrain.autenticacao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Collections;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    public static String APP_CLIENT = "xbrain-app-client";

    @Value("${keys.private}")
    private String privateKey;
    @Value("${keys.public}")
    private String publicKey;
    @Value("${app-config.oauth-clients.autenticacao-api.client}")
    private String autenticacaoApiClient;
    @Value("${app-config.oauth-clients.autenticacao-api.secret}")
    private String autenticacaoApiSecret;
    @Value("${app-config.oauth-clients.front-apps.client}")
    private String frontAppsClient;
    @Value("${app-config.oauth-clients.front-apps.secret}")
    private String frontAppsSecret;
    @Value("${app-config.oauth-clients.parceiros-online-api.client}")
    private String parceirosApiClient;
    @Value("${app-config.oauth-clients.parceiros-online-api.secret}")
    private String parceirosApiSecret;
    @Value("${app-config.oauth-clients.vendas-api.client}")
    private String vendasApiClient;
    @Value("${app-config.oauth-clients.vendas-api.secret}")
    private String vendasApiSecret;
    @Value("${app-config.oauth-clients.mailing-api.client}")
    private String mailingApiClient;
    @Value("${app-config.oauth-clients.mailing-api.secret}")
    private String mailingApiSecret;
    @Value("${app-config.oauth-clients.equipe-venda-api.client}")
    private String equipeVendaApiClient;
    @Value("${app-config.oauth-clients.equipe-venda-api.secret}")
    private String equipeVendaApiSecret;

    private static final int UM_MES_EM_SEGUNDOS = 2592000;

    @Autowired
    private CustomTokenEndpointAuthenticationFilter customTokenEndpointAuthenticationFilter;
    @Autowired
    private Environment environment;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenStore tokenStore;

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter tokenConverter = new CustomJwtAccessTokenConverter();
        tokenConverter.setSigningKey(privateKey);
        tokenConverter.setVerifierKey(publicKey);
        return tokenConverter;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(frontAppsClient)
                .secret(frontAppsSecret)
                .authorizedGrantTypes("password")
                .scopes("app")
                .accessTokenValiditySeconds(UM_MES_EM_SEGUNDOS)
                .and()
                .withClient(autenticacaoApiClient)
                .secret(autenticacaoApiSecret)
                .authorizedGrantTypes("client_credentials")
                .scopes("autenticacao-api")
                .and()
                .withClient(parceirosApiClient)
                .secret(parceirosApiSecret)
                .authorizedGrantTypes("client_credentials")
                .scopes("parceiros-api")
                .and()
                .withClient(vendasApiClient)
                .secret(vendasApiSecret)
                .authorizedGrantTypes("client_credentials")
                .scopes("vendas-api")
                .and()
                .withClient(mailingApiClient)
                .secret(mailingApiSecret)
                .authorizedGrantTypes("client_credentials")
                .scopes("mailing-api")
                .and()
                .withClient(equipeVendaApiClient)
                .secret(equipeVendaApiSecret)
                .authorizedGrantTypes("client_credentials")
                .scopes("equipevenda-api")
                .authorities("ROLE_APPLICATION");
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()");
        if (environment.acceptsProfiles("!test")) {
            oauthServer.tokenEndpointAuthenticationFilters(Collections.singletonList(customTokenEndpointAuthenticationFilter));
        }
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .tokenStore(tokenStore)
                .accessTokenConverter(jwtAccessTokenConverter())
                .authenticationManager(authenticationManager);
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomJwtAccessTokenConverter();
    }

    @Bean
    public CustomTokenEndpointAuthenticationFilter customFilter() {
        return new CustomTokenEndpointAuthenticationFilter();
    }
}
