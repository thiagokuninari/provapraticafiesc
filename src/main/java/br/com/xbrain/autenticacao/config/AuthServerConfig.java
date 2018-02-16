package br.com.xbrain.autenticacao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Value("${keys.private}")
    private String privateKey;
    @Value("${keys.public}")
    private String publicKey;
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

    @Autowired
    private CustomJdbcTokenStore customJdbcTokenStore;

    private static final int UM_MES_EM_SEGUNDOS = 2592000;

    @Autowired
    private AuthenticationManager authenticationManager;

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
                .scopes("mailing-api");
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(customJdbcTokenStore)
                .accessTokenConverter(jwtAccessTokenConverter())
                .authenticationManager(authenticationManager);
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomJwtAccessTokenConverter();
    }
}
