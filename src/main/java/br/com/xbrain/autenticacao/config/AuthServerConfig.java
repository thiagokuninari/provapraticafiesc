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

import static br.com.xbrain.autenticacao.config.EScopes.APP;
import static br.com.xbrain.autenticacao.config.EScopes.AUTENTICACAO;
import static br.com.xbrain.autenticacao.config.EScopes.PARCEIROS_ONLINE;
import static br.com.xbrain.autenticacao.config.EScopes.VENDAS;
import static br.com.xbrain.autenticacao.config.EScopes.INTEGRACAO_VENDAS;
import static br.com.xbrain.autenticacao.config.EScopes.INTEGRACAO_BRSCAN;
import static br.com.xbrain.autenticacao.config.EScopes.MAILING;
import static br.com.xbrain.autenticacao.config.EScopes.MAILING_DISCADORA;
import static br.com.xbrain.autenticacao.config.EScopes.MAILING_IMPORTACAO;
import static br.com.xbrain.autenticacao.config.EScopes.EQUIPE_VENDA;
import static br.com.xbrain.autenticacao.config.EScopes.CALL;
import static br.com.xbrain.autenticacao.config.EScopes.DASHBOARD;
import static br.com.xbrain.autenticacao.config.EScopes.DISCADORA_ECCP;
import static br.com.xbrain.autenticacao.config.EScopes.CONTATO_CRN;
import static br.com.xbrain.autenticacao.config.EScopes.CHAMADO;
import static br.com.xbrain.autenticacao.config.EScopes.FUNIL_PROSPECCAO;
import static br.com.xbrain.autenticacao.config.EScopes.DISCADORA;
import static br.com.xbrain.autenticacao.config.EScopes.ASTERISK_URA;
import static br.com.xbrain.autenticacao.config.EScopes.INDICACAO;
import static br.com.xbrain.autenticacao.config.EScopes.GERADOR_LEAD;

@Configuration
@EnableAuthorizationServer
@SuppressWarnings("PMD.TooManyStaticImports")
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    private static final int UM_MES_EM_SEGUNDOS = 2592000;
    private static final String ROLE_APPLICATION = "ROLE_APPLICATION";
    private static final String PASSWORD = "password";
    private static final String CLIENT_CREDENTIALS = "client_credentials";
    public static final String APP_CLIENT = "xbrain-app-client";

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
    @Value("${app-config.oauth-clients.indicacao-api.client}")
    private String indicacaoApiClient;
    @Value("${app-config.oauth-clients.indicacao-api.secret}")
    private String indicacaoApiSecret;
    @Value("${app-config.oauth-clients.backoffice-api.client}")
    private String backofficeApiClient;
    @Value("${app-config.oauth-clients.backoffice-api.secret}")
    private String backofficeApiSecret;
    @Value("${app-config.oauth-clients.gerador-lead-api.client}")
    private String geradorLeadApiClient;
    @Value("${app-config.oauth-clients.gerador-lead-api.secret}")
    private String geradorLeadApiSecret;
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
            .authorizedGrantTypes(PASSWORD)
            .scopes(APP.getScope())
            .accessTokenValiditySeconds(UM_MES_EM_SEGUNDOS)
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(autenticacaoApiClient)
            .secret(autenticacaoApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(AUTENTICACAO.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(parceirosApiClient)
            .secret(parceirosApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(PARCEIROS_ONLINE.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(vendasApiClient)
            .secret(vendasApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(VENDAS.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(integracaoVendasApiClient)
            .secret(integracaoVendasApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(INTEGRACAO_VENDAS.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(integracaoBrScanApiClient)
            .secret(integracaoBrScanApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(INTEGRACAO_BRSCAN.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(mailingApiClient)
            .secret(mailingApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(MAILING.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(mailingDiscadoraApiClient)
            .secret(mailingDiscadoraApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(MAILING_DISCADORA.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(mailingImportacaoApiClient)
            .secret(mailingImportacaoApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(MAILING_IMPORTACAO.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(equipeVendaApiClient)
            .secret(equipeVendaApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(EQUIPE_VENDA.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(callApiClient)
            .secret(callApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(CALL.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(dashboardApiClient)
            .secret(dashboardApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(DASHBOARD.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(discadoraEccpApiClient)
            .secret(discadoraEccpApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(DISCADORA_ECCP.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(contatoCrnApiClient)
            .secret(contatoCrnApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(CONTATO_CRN.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(chamadoApiClient)
            .secret(chamadoApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(CHAMADO.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(funilProspeccaoApiClient)
            .secret(funilProspeccaoApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(FUNIL_PROSPECCAO.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(discadoraApiClient)
            .secret(discadoraApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(DISCADORA.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(asteriskUraApiClient)
            .secret(asteriskuraApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(ASTERISK_URA.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(indicacaoApiClient)
            .secret(indicacaoApiSecret)
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
            .scopes(INDICACAO.getScope())
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(backofficeApiClient)
            .secret(backofficeApiSecret)
            .authorizedGrantTypes("client_credentials")
            .authorities(ROLE_APPLICATION)
            .and()
            .withClient(geradorLeadApiClient)
            .secret(geradorLeadApiSecret)
            .scopes(GERADOR_LEAD.getScope())
            .authorizedGrantTypes(CLIENT_CREDENTIALS)
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
