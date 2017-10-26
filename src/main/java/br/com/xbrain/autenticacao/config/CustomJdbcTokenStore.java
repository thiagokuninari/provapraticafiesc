package br.com.xbrain.autenticacao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class CustomJdbcTokenStore extends JdbcTokenStore {

    @Autowired
    public CustomJdbcTokenStore(DataSource dataSource) {
        super(dataSource);
    }
}
