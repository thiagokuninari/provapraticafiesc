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

    private static final int UM_MES_EM_SEGUNDOS = 2592000;
    private static final String ROLE_APPLICATION = "ROLE_APPLICATION";
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
    @Value("${app-config.oauth-clients.integracao-vendas-api.client}")
    private String integracaoVendasApiClient;
    @Value("${app-config.oauth-clients.integracao-vendas-api.secret}")
    private String integracaoVendasApiSecret;
    @Value("${app-config.oauth-clients.integracao-brscan-api.client}")
    private String integracaoBrScanApiClient;
    @Value("${app-config.oauth-clients.integracao-brscan-api.secret}")
    private String integracaoBrScanApiSecret;
    @Value("${app-config.oauth-clients.mailing-api.client}")
    private String mailingApiClient;
    @Value("${app-config.oauth-clients.mailing-api.secret}")
    private String mailingApiSecret;
    @Value("${app-config.oauth-clients.mailing-discadora-api.client}")
    private String mailingDiscadoraApiClient;
    @Value("${app-config.oauth-clients.mailing-discadora-api.secret}")
    private String mailingDiscadoraApiSecret;
    @Value("${app-config.oauth-clients.mailing-importacao-api.client}")
    private String mailingImportacaoApiClient;
    @Value("${app-config.oauth-clients.mailing-importacao-api.secret}")
    private String mailingImportacaoApiSecret;
    @Value("${app-config.oauth-clients.equipe-venda-api.client}")
    private String equipeVendaApiClient;
    @Value("${app-config.oauth-clients.equipe-venda-api.secret}")
    private String equipeVendaApiSecret;
    @Value("${app-config.oauth-clients.call-api.client}")
    private String callApiClient;
    @Value("${app-config.oauth-clients.call-api.secret}")
    private String callApiSecret;
    @Value("${app-config.oauth-clients.dashboard-api.client}")
    private String dashboardApiClient;
    @Value("${app-config.oauth-clients.dashboard-api.secret}")
    private String dashboardApiSecret;
    @Value("${app-config.oauth-clients.contato-crn-api.client}")
    private String contatoCrnApiClient;
    @Value("${app-config.oauth-clients.contato-crn-api.secret}")
    private String contatoCrnApiSecret;
    @Value("${app-config.oauth-clients.discadora-eccp-api.client}")
    private String discadoraEccpApiClient;
    @Value("${app-config.oauth-clients.discadora-eccp-api.secret}")
    private String discadoraEccpApiSecret;
    @Value("${app-config.oauth-clients.chamado-api.client}")
    private String chamadoApiClient;
    @Value("${app-config.oauth-clients.chamado-api.secret}")
    private String chamadoApiSecret;
    @Value("${app-config.oauth-clients.funil-prospeccao-api.client}")
    private String funilProspeccaoApiClient;
    @Value("${app-config.oauth-clients.funil-prospeccao-api.secret}")
    private String funilProspeccaoApiSecret;
    @Value("${app-config.oauth-clients.discadora-api.client}")
    private String discadoraApiClient;
    @Value("${app-config.oauth-clients.discadora-api.secret}")
    private String discadoraApiSecret;
    @Value("${app-config.oauth-clients.asterisk-ura-api.client}")
    private String asteriskUraApiClient;
    @Value("${app-config.oauth-clients.asterisk-ura-api.secret}")
    private String asteriskuraApiSecret;
    @Value("${app-config.oauth-clients.click-to-call-ativo-api.client}")
    private String clickToCallAtivoApiClient;
    @Value("${app-config.oauth-clients.click-to-call-ativo-api.secret}")
    private String clickToCallAtivoApiSecret;
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
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(autenticacaoApiClient)
            .secret(autenticacaoApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("autenticacao-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(parceirosApiClient)
            .secret(parceirosApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("parceiros-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(vendasApiClient)
            .secret(vendasApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("vendas-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(integracaoVendasApiClient)
            .secret(integracaoVendasApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("integracao-vendas-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(integracaoBrScanApiClient)
            .secret(integracaoBrScanApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("integracao-brscan-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(mailingApiClient)
            .secret(mailingApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("mailing-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(mailingDiscadoraApiClient)
            .secret(mailingDiscadoraApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("mailing-discadora-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(mailingImportacaoApiClient)
            .secret(mailingImportacaoApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("mailing-importacao-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(equipeVendaApiClient)
            .secret(equipeVendaApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("equipevenda-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(callApiClient)
            .secret(callApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("call-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(dashboardApiClient)
            .secret(dashboardApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("dashboard-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(discadoraEccpApiClient)
            .secret(discadoraEccpApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("discadora-eccp-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(contatoCrnApiClient)
            .secret(contatoCrnApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("contato-crn-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(chamadoApiClient)
            .secret(chamadoApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("chamado-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(funilProspeccaoApiClient)
            .secret(funilProspeccaoApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("funil-prospeccao-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(discadoraApiClient)
            .secret(discadoraApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("discadora-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(asteriskUraApiClient)
            .secret(asteriskuraApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("asterisk-ura-api")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(clickToCallAtivoApiClient)
            .secret(clickToCallAtivoApiSecret)
            .authorizedGrantTypes("client_credentials")
            .scopes("click-to-call-ativo-api")
            .authorities(ROLE_APPLICATION);
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
