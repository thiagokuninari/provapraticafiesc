package br.com.xbrain.autenticacao.modules.permissao.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import static java.util.Collections.singletonList;

@Service
public class ClientCredentialsTokenService {

    @Value("${app-config.services.autenticacao.url}")
    private String autenticacaoUrl;
    @Value("${app-config.oauth-clients.autenticacao-api.client}")
    private String oauthClient;
    @Value("${app-config.oauth-clients.autenticacao-api.secret}")
    private String oauthClientSecret;

    private ClientCredentialsResourceDetails resourceDetails() {
        ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
        details.setClientId(oauthClient);
        details.setClientSecret(oauthClientSecret);
        details.setScope(singletonList("autenticacao-api"));
        details.setAuthenticationScheme(AuthenticationScheme.form);
        details.setAccessTokenUri(autenticacaoUrl + "/oauth/token");
        details.setClientAuthenticationScheme(AuthenticationScheme.header);
        return details;
    }

    public OAuth2AccessToken getToken() {
        // TODO cachear a token e tratar quando a token for removida da base
        return new ClientCredentialsAccessTokenProvider()
                .obtainAccessToken(resourceDetails(), new DefaultAccessTokenRequest());
    }
}
